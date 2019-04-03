package com.hawkstech.intellij.plugin.fernflower

import com.google.common.base.Preconditions.checkNotNull
import com.google.common.base.Preconditions.checkState
import com.google.common.base.Strings
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.ide.util.PropertiesComponent
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.application.ex.ApplicationManagerEx
import com.intellij.openapi.command.WriteCommandAction.writeCommandAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.module.Module
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.*
import com.intellij.openapi.roots.libraries.Library
import com.intellij.openapi.roots.ui.configuration.LibrarySourceRootDetectorUtil
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.*
import com.intellij.ui.components.LegalNoticeDialog
import com.intellij.util.CommonProcessors
import org.jetbrains.annotations.NotNull
import org.jetbrains.java.decompiler.IdeaDecompilerBundle
import org.jetbrains.java.decompiler.main.DecompilerContext
import org.jetbrains.java.decompiler.main.Fernflower
import org.jetbrains.java.decompiler.main.extern.IBytecodeProvider
import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger
import org.jetbrains.java.decompiler.main.extern.IResultSaver
import org.jetbrains.java.decompiler.util.InterpreterUtil
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.jar.JarOutputStream
import java.util.jar.Manifest
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream
import kotlin.collections.HashMap

/**
 * Created by bduisenov on 12/11/15. as DecompileAndAttacheAction
 * updated rferguson 12/9/17 and 9/2/18.
 * expanded with extra function awhawks 4/3/2019
 */
class DecompileWithOptionsPlugin: AnAction(),IBytecodeProvider, IResultSaver {
	private val logger = Logger.getInstance(DecompileWithOptionsPlugin::class.java)
	private val baseDirProjectSettingsKey = "com.hawkstech.intellij.plugin.fernflower.baseDir"
	private val srcDirProjectSettingsKey  = "com.hawkstech.intellij.plugin.fernflower.srcDir"

	// From IdeaDecompiler
	private val legalNoticeKey  = "decompiler.legal.notice.accepted"
	private val declineExitCode = DialogWrapper.NEXT_USER_EXIT_CODE

	private var isAttachOption:Boolean = false
	private var decompilerOptions:Map<String, Any> = HashMap()
	private var tmpDir:File = File("dstNotSetYet")

	/**
	 * show 'decompile and attach' option only for *.jar files
	 * @param e AnActionEvent
	 */
	override fun update(e: AnActionEvent) {
		val presentation = e.presentation
		presentation.isEnabled = false
		presentation.isVisible = false
		val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
		if (virtualFile != null &&
				"jar" == virtualFile.extension &&
				e.project != null) {
			presentation.isEnabled = true
			presentation.isVisible = true
		}
	}

	override fun actionPerformed(event: AnActionEvent) {
		val project = event.project ?: return

		if (!PropertiesComponent.getInstance().isValueSet(legalNoticeKey)) {
			val title   = IdeaDecompilerBundle.message("legal.notice.title", "Legal Terms")
			val message = IdeaDecompilerBundle.message("legal.notice.text")
			val answer = LegalNoticeDialog.build(title, message)
					.withCancelText("Decide Later")
					.withCustomAction("Decline and restart", declineExitCode)
					.show()
			when (answer) {
				DialogWrapper.OK_EXIT_CODE -> {
					PropertiesComponent.getInstance().setValue(legalNoticeKey, true)
					logger.info("Decompiler legal notice accepted.")
				}
				declineExitCode -> {
					PluginManagerCore.disablePlugin("com.hawkstech.intellij.plugin.fernflowerdecompiler")
					ApplicationManagerEx.getApplicationEx().restart(true)
					logger.info("Decompiler legal notice rejected, disabling decompile and attach plugin.")
					return
				}
				else -> {
					Notification("DecompileAndAttach", "Decompile request rejected",
							"Decompiler cannot continue until terms of use are accepted.", NotificationType.INFORMATION)
							.notify(project)
					return
				}
			}
		}

		val baseDirPath = getBaseDirPath(project, baseDirProjectSettingsKey, "Select Project Specific Folder For Storing Decompiled Sources")
		if (!baseDirPath.isPresent) {
			return
		}
		val srcDirPath = getBaseDirPath(project, srcDirProjectSettingsKey, "Select directory for extracted source Files")
		if (!srcDirPath.isPresent) {
			return
		}
		val sourceVFs = event.getData(PlatformDataKeys.VIRTUAL_FILE_ARRAY)
		checkState(sourceVFs != null && sourceVFs.isNotEmpty(), "event#getData(VIRTUAL_FILE_ARRAY) returned empty array")

		val content = DecompileOptionsUI(project)

		if(content.showAndGet()) {
			isAttachOption    = content.getAttachOption()
			decompilerOptions = content.getParsedOptions()
			object : Task.Backgroundable(project, "Decompiling...", true) {
				override fun run(@NotNull indicator: ProgressIndicator) {
					indicator.fraction = 0.1

					sourceVFs!!.toList().stream() //
							.filter { vf -> "jar" == vf.extension } //
							.forEach { sourceVF ->
								process(project, baseDirPath.get(), srcDirPath.get(), sourceVF, indicator, 1.0 / sourceVFs.size)
							}

					indicator.fraction = 1.0
					VirtualFileManager.getInstance().getFileSystem(StandardFileSystems.FILE_PROTOCOL).refresh(true)
				}

				override fun shouldStartInBackground(): Boolean {
					return true
				}
			}.queue()
		}
	}

	private fun getBaseDirPath(project: Project, key:String, title:String ): Optional<String> {
		//val result: String = FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFolderDescriptor().withTitle(title), project, null)!!.path
		var result:String? = null
        val baseDirPath:String = PropertiesComponent.getInstance(project).getValue(key)?:""
        if (Strings.isNullOrEmpty(baseDirPath)) {
            val form = FolderSelectionForm(project, title)
            if (form.showAndGet()) {
                result = form.selectedPath
                PropertiesComponent.getInstance(project).setValue(key, result)
            }
        } else {
            result = baseDirPath
        }
        return Optional.ofNullable(result)
	}

	private fun process(project: Project, baseDirPath: String, srcDirPath: String, sourceVF: VirtualFile, indicator: ProgressIndicator, fractionStep: Double):File {
		indicator.text = "Decompiling '" + sourceVF.name + "'"
		val libraryName = sourceVF.name.replace(".jar", "-sources.jar")
		val fullPath = baseDirPath + File.separator + libraryName
		var outputFile = File(fullPath)
		val jarFileSystemInstance = JarFileSystem.getInstance()
		val jarRoot = jarFileSystemInstance.getJarRootForLocalFile(sourceVF)?: jarFileSystemInstance.getJarRootForLocalFile(Objects.requireNonNull<VirtualFile>(jarFileSystemInstance.getVirtualFileForJar(sourceVF)))!!

		try {
			tmpDir = FileUtil.createTempDirectory("decompiledTempDIR","ext")
			val tmpJarFile  = File(tmpDir, sourceVF.name )
			val srcJar = File(jarRoot.canonicalPath!!.dropLast(2))

			try {
				val fernLogger = DecompileWithOptionsLogger()
				//val fernLogger = PrintStreamLogger(System.out)
				val engine = Fernflower(this, this, decompilerOptions, fernLogger)
				engine.addSource(srcJar)
				try {
					engine.decompileContext()
				} finally {
					engine.clearContext()
				}
				if (outputFile.exists()) {
					FileUtil.deleteWithRenaming(outputFile)
					outputFile = File(fullPath)
					outputFile.createNewFile()
				}
			} catch (e:Exception){
				Notification("DecompileWithOptions", "Jar lib couldn't be decompiled",
						"Fernflower args: $decompilerOptions ${srcJar.absolutePath} ${tmpDir.absolutePath}", NotificationType.ERROR).notify(project)
				FileUtil.delete(tmpJarFile)
				FileUtil.delete(tmpDir)
			} finally {
				if(tmpJarFile.exists()){
					FileUtil.copy(tmpJarFile, outputFile)
					if(isAttachOption) {
						attach(project, sourceVF, outputFile)
					}
					indicator.fraction = indicator.fraction + fractionStep * 30 / 100
					FileUtil.delete(tmpJarFile)
					FileUtil.delete(tmpDir)
					ZipFile(outputFile.absolutePath).use { zip ->
						zip.entries().asSequence().forEach { entry ->
							if(entry.isDirectory){
								val dstDir = File("$srcDirPath/${entry.name}")
								if (!dstDir.exists()) {
									dstDir.parentFile.mkdirs()
								}
							} else {
								zip.getInputStream(entry).use { input ->
									val dstFile = File("$srcDirPath/${entry.name}")
									if (!dstFile.exists()) {
										dstFile.parentFile.mkdirs()
										dstFile.createNewFile()
									}
									if (dstFile.isFile) {
										dstFile.outputStream().use { output ->
											input.copyTo(output)
										}
									}
								}
							}
						}
					}
				} else {
					Notification("DecompileWithOptions", "Jar lib couldn't be decompiled",
							"Fernflower args: ${tmpJarFile.absolutePath} was not generated", NotificationType.ERROR).notify(project)
				}
			}
		} catch (e: IOException) {
			Notification("DecompileWithOptions", "Jar lib couldn't be decompiled",
					sourceVF.name + " " + e.javaClass.name + " " + e.toString(), NotificationType.ERROR).notify(project)
		}
		return outputFile
	}

	private fun attach(project: Project?, sourceVF: VirtualFile, resultJar: File) {
		ApplicationManager.getApplication().invokeAndWait({
			val resultJarVF = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(resultJar)!!
			checkNotNull<VirtualFile>(resultJarVF, "could not find Virtual File of %s", resultJar.absolutePath)
			val resultJarRoot = JarFileSystem.getInstance().getJarRootForLocalFile(resultJarVF)!!
			val roots = LibrarySourceRootDetectorUtil.scanAndSelectDetectedJavaSourceRoots(null,
					arrayOf<VirtualFile>(resultJarRoot))
			writeCommandAction(project).run<RuntimeException> {

				val currentModule = ProjectRootManager.getInstance(project!!).fileIndex .getModuleForFile(sourceVF, false)

				if (currentModule != null) {
					val moduleLib = findModuleDependency(currentModule, sourceVF)
					checkState(moduleLib.isPresent, "could not find library in module dependencies")
					val model = moduleLib.get().modifiableModel
					for (root in roots) {
						model.addRoot(root, OrderRootType.SOURCES)
					}
					model.commit()

					Notification("DecompileAndAttach", "Jar Sources Added", "decompiled sources " + resultJar.name
							+ " where added successfully to dependency of a module '" + currentModule.name + "'",
							NotificationType.INFORMATION).notify(project)
				} else if ("jar" != sourceVF.extension) {
					Notification("DecompileAndAttach", "Failed getModule",
							"File is " + sourceVF.name, NotificationType.WARNING).notify(project)
				}
			}
		}, ModalityState.NON_MODAL)
	}

	private fun findModuleDependency(module: Module, sourceVF: VirtualFile): Optional<Library> {
		val processor = object : CommonProcessors.FindProcessor<OrderEntry>() {

			override fun accept(orderEntry: OrderEntry): Boolean {
				val urls = orderEntry.getUrls(OrderRootType.CLASSES)
				val contains = Arrays.asList(*urls).contains("jar://" + sourceVF.path + "!/")
				return contains && orderEntry is LibraryOrderEntry
			}
		}
		ModuleRootManager.getInstance(module).orderEntries().forEach(processor)
		var result: Library? = null
		if (processor.foundValue != null) {
			result = (processor.foundValue as LibraryOrderEntry).library
		}
		return Optional.ofNullable(result)
	}

	private val mapArchiveStreams = HashMap<String, ZipOutputStream>()
	private val mapArchiveEntries = HashMap<String, Set<String>>()

	// *******************************************************************
	// Utility functions used by Interface IResultSaver
	// *******************************************************************
	private fun getAbsolutePath(path: String): String {
		return File(tmpDir, path).absolutePath
	}

	private fun checkEntry(entryName: String, file: String): Boolean {
		val set:Set<String> = mapArchiveEntries.getOrPut(file) { HashSet() }
		val added = !set.contains(entryName)
		set.plus( entryName )
		if (!added) {
			val message = "Zip file $file already has entry $entryName"
			DecompilerContext.getLogger().writeMessage(message, IFernflowerLogger.Severity.WARN)
		}
		return added
	}

	// *******************************************************************
	// Interface IBytecodeProvider
	// *******************************************************************

	@Throws(IOException::class)
	override fun getBytecode(externalPath: String, internalPath: String?): ByteArray {
		val file = File(externalPath)
		if (internalPath == null) {
			return InterpreterUtil.getBytes(file)
		} else {
			ZipFile(file).use { archive ->
				val entry = archive.getEntry(internalPath) ?: throw IOException("Entry not found: $internalPath")
				return InterpreterUtil.getBytes(archive, entry)
			}
		}
	}

	// *******************************************************************
	// Interface IResultSaver
	// *******************************************************************

	override fun saveFolder(path: String) {
		val dir = File(getAbsolutePath(path))
		if (!(dir.mkdirs() || dir.isDirectory)) {
			throw RuntimeException("Cannot create directory $dir")
		}
	}

	override fun closeArchive(path: String, archiveName: String) {
		val file = File(getAbsolutePath(path), archiveName).path
		try {
			mapArchiveEntries.remove(file)
			mapArchiveStreams.remove(file)?.close()
		} catch (ex: IOException) {
			DecompilerContext.getLogger().writeMessage("Cannot close $file", IFernflowerLogger.Severity.WARN)
		}
	}

	override fun copyFile(source: String, path: String, entryName: String) {
		try {
			InterpreterUtil.copyFile(File(source), File(getAbsolutePath(path), entryName))
		} catch (ex: IOException) {
			DecompilerContext.getLogger().writeMessage("Cannot copy $source to $entryName", ex)
		}
	}

	override fun copyEntry(source: String, path: String, archiveName: String, entryName: String) {
		val file = File(getAbsolutePath(path), archiveName).path

		if (!checkEntry(entryName, file)) {
			return
		}

		try {
			ZipFile(File(source)).use { srcArchive ->
				val entry = srcArchive.getEntry(entryName)
				if (entry != null) {
					srcArchive.getInputStream(entry).use { `in` ->
						val out = mapArchiveStreams[file]!!
						out.putNextEntry(ZipEntry(entryName))
						InterpreterUtil.copyStream(`in`, out)
					}
				}
			}
		} catch (ex: IOException) {
			val message = "Cannot copy entry $entryName from $source to $file"
			DecompilerContext.getLogger().writeMessage(message, ex)
		}
	}

	override fun saveClassEntry(path: String, archiveName: String, qualifiedName: String?, entryName: String, content: String?) {
		val file = File(getAbsolutePath(path), archiveName).path

		if (!checkEntry(entryName, file)) {
			return
		}

		try {
			val out = mapArchiveStreams[file]!!
			out.putNextEntry(ZipEntry(entryName))
			if (content != null) {
				out.write(content.toByteArray(StandardCharsets.UTF_8))
			}
		} catch (ex: IOException) {
			val message = "Cannot write entry $entryName to $file"
			DecompilerContext.getLogger().writeMessage(message, ex)
		}
	}

	override fun createArchive(path: String, archiveName: String, manifest: Manifest?) {
		val file = File(getAbsolutePath(path), archiveName)
		try {
			if (!(file.createNewFile() || file.isFile)) {
				throw IOException("Cannot create file $file")
			}

			val fileStream = FileOutputStream(file)
			val zipStream = manifest?.let { JarOutputStream(fileStream, it) } ?: ZipOutputStream(fileStream)
			mapArchiveStreams[file.path] = zipStream
		} catch (ex: IOException) {
			DecompilerContext.getLogger().writeMessage("Cannot create archive $file", ex)
		}
	}

	override fun saveClassFile(path: String, qualifiedName: String, entryName: String, content: String, mapping: IntArray) {
		val file = File(getAbsolutePath(path), entryName)
		try {
			OutputStreamWriter(FileOutputStream(file), StandardCharsets.UTF_8).use { out -> out.write(content) }
		} catch (ex: IOException) {
			DecompilerContext.getLogger().writeMessage("Cannot write class file $file", ex)
		}
	}

	override fun saveDirEntry(path: String, archiveName: String, entryName: String) {
		saveClassEntry(path, archiveName, null, entryName, null)
	}


}
