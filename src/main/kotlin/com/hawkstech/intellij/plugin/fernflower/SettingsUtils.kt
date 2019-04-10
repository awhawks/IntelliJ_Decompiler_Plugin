package com.hawkstech.intellij.plugin.fernflower

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

class SettingsUtils(val project:Project, val selectedJarFolder:VirtualFile) {
	public enum class SettingNames {
		REMOVE_BRIDGE,
		REMOVE_SYNTHETIC,
		DECOMPILE_INNER,
		DECOMPILE_CLASS_1_4,
		DECOMPILE_ASSERTIONS,
		HIDE_EMPTY_SUPER,
		HIDE_DEFAULT_CONSTRUCTOR,
		DECOMPILE_GENERIC_SIGNATURES,
		NO_EXCEPTIONS_RETURN,
		ENSURE_SYNCHRONIZED_MONITOR,
		DECOMPILE_ENUM,
		REMOVE_GET_CLASS_NEW,
		LITERALS_AS_IS,
		BOOLEAN_TRUE_ONE,
		ASCII_STRING_CHARACTERS,
		SYNTHETIC_NOT_SET,
		UNDEFINED_PARAM_TYPE_OBJECT,
		USE_DEBUG_VAR_NAMES,
		USE_METHOD_PARAMETERS,
		REMOVE_EMPTY_RANGES,
		FINALLY_DEINLINE,
		IDEA_NOT_NULL_ANNOTATION,
		LAMBDA_TO_ANONYMOUS_CLASS,
		BYTECODE_SOURCE_MAPPING,
		IGNORE_INVALID_BYTECODE,
		VERIFY_ANONYMOUS_CLASSES,

		LOG_LEVEL,
		MAX_PROCESSING_METHOD,
		RENAME_ENTITIES,
		USER_RENAMER_CLASS,
		NEW_LINE_SEPARATOR,
		INDENT_STRING,
		BANNER,

		DUMP_ORIGINAL_LINES,
		UNIT_TEST_MODE,

		// my options
		ATTACH_SOURCE_JAR,
		DECOMPILED_JAR_DIR,
		DECOMPILED_SRC_DIR

	}
	private val propertyComponent = PropertiesComponent.getInstance(project)
	private val projectSettingsPrefix = "com.hawkstech.intellij.plugin.fernflower"

	fun getProperty(key:SettingNames, default:String):String {
		return when(key){
			SettingNames.DECOMPILED_JAR_DIR -> propertyComponent.getValue("${projectSettingsPrefix}.${key.name}")?:selectedJarFolder.path
			SettingNames.DECOMPILED_SRC_DIR -> propertyComponent.getValue("${projectSettingsPrefix}.${key.name}")?:selectedJarFolder.path
			else ->  propertyComponent.getValue("${projectSettingsPrefix}.${key.name}")?:default
		}
	}

	fun setProperty(key:SettingNames, value:String){
		PropertiesComponent.getInstance(project).setValue("${projectSettingsPrefix}.${key.name}", value)
	}
}
