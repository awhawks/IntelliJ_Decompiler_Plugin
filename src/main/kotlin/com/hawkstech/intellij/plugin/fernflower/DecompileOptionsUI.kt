package com.hawkstech.intellij.plugin.fernflower

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger
import org.jetbrains.java.decompiler.util.InterpreterUtil
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.*
import kotlin.math.max
import com.intellij.openapi.fileChooser.*
import com.intellij.openapi.ui.*


class DecompileOptionsUI(project:Project, private val storedSettings:SettingsUtils ):DialogWrapper(project) {
	private val logger = Logger.getInstance(DecompileOptionsUI::class.java)
	private val optionsMap:MutableMap<SettingsUtils.SettingNames, String> = mutableMapOf()
	private var attachOption = false
	private var mainJPanel:JPanel
	private val attach:JRadioButton
	private var asc:JRadioButton
	private var bsm:JRadioButton
	private var bto:JRadioButton
	private var das:JRadioButton
	private var dc4:JRadioButton
	private var den:JRadioButton
	private var dgs:JRadioButton
	private var din:JRadioButton
	private var esm:JRadioButton
	private var fdi:JRadioButton
	private var hdc:JRadioButton
	private var hes:JRadioButton
	private var iib:JRadioButton
	private var inn:JRadioButton
	private var lac:JRadioButton
	private var lit:JRadioButton
	private var ner:JRadioButton
	private var nls:JRadioButton
	private var nss:JRadioButton
	private var rbr:JRadioButton
	private var ren:JRadioButton
	private var rer:JRadioButton
	private var rgn:JRadioButton
	private var rsy:JRadioButton
	private var udv:JRadioButton
	private var ump:JRadioButton
	private var uto:JRadioButton
	private var vac:JRadioButton
	private var ind:JTextField
	private var mpm:JTextField
	//var urc:JTextField
	private var ban:JTextArea
	private var log:ComboBox<String>

	private var decompiledJarFolder:TextFieldWithBrowseButton
	private var decompiledJarFolderString:String = ""

	private var decompiledSrcDir:TextFieldWithBrowseButton
	private var decompiledSrcFolderString:String = ""

	fun getAttachOption():Boolean {
		return attachOption
	}

	fun getDecompiledJarDir():String {
		val newValue = decompiledJarFolder.text
		storedSettings.setProperty(SettingsUtils.SettingNames.DECOMPILED_JAR_DIR, newValue)
		return newValue
	}

	fun getDecompiledSrcDir():String {
		val newValue = decompiledSrcDir.text
		storedSettings.setProperty(SettingsUtils.SettingNames.DECOMPILED_SRC_DIR, newValue)
		return newValue
	}

	fun getParsedOptions():Map<SettingsUtils.SettingNames, String> {
		return mapOf(
				SettingsUtils.SettingNames.ASCII_STRING_CHARACTERS      to optionsMap[SettingsUtils.SettingNames.ASCII_STRING_CHARACTERS     ]!!,
				SettingsUtils.SettingNames.BOOLEAN_TRUE_ONE             to optionsMap[SettingsUtils.SettingNames.BOOLEAN_TRUE_ONE            ]!!,
				SettingsUtils.SettingNames.BYTECODE_SOURCE_MAPPING      to optionsMap[SettingsUtils.SettingNames.BYTECODE_SOURCE_MAPPING     ]!!,
				SettingsUtils.SettingNames.DECOMPILE_ASSERTIONS         to optionsMap[SettingsUtils.SettingNames.DECOMPILE_ASSERTIONS        ]!!,
				SettingsUtils.SettingNames.DECOMPILE_CLASS_1_4          to optionsMap[SettingsUtils.SettingNames.DECOMPILE_CLASS_1_4         ]!!,
				SettingsUtils.SettingNames.DECOMPILE_ENUM               to optionsMap[SettingsUtils.SettingNames.DECOMPILE_ENUM              ]!!,
				SettingsUtils.SettingNames.DECOMPILE_GENERIC_SIGNATURES to optionsMap[SettingsUtils.SettingNames.DECOMPILE_GENERIC_SIGNATURES]!!,
				SettingsUtils.SettingNames.DECOMPILE_INNER              to optionsMap[SettingsUtils.SettingNames.DECOMPILE_INNER             ]!!,
				SettingsUtils.SettingNames.ENSURE_SYNCHRONIZED_MONITOR  to optionsMap[SettingsUtils.SettingNames.ENSURE_SYNCHRONIZED_MONITOR ]!!,
				SettingsUtils.SettingNames.FINALLY_DEINLINE             to optionsMap[SettingsUtils.SettingNames.FINALLY_DEINLINE            ]!!,
				SettingsUtils.SettingNames.HIDE_DEFAULT_CONSTRUCTOR     to optionsMap[SettingsUtils.SettingNames.HIDE_DEFAULT_CONSTRUCTOR    ]!!,
				SettingsUtils.SettingNames.HIDE_EMPTY_SUPER             to optionsMap[SettingsUtils.SettingNames.HIDE_EMPTY_SUPER            ]!!,
				SettingsUtils.SettingNames.IDEA_NOT_NULL_ANNOTATION     to optionsMap[SettingsUtils.SettingNames.IDEA_NOT_NULL_ANNOTATION    ]!!,
				SettingsUtils.SettingNames.IGNORE_INVALID_BYTECODE      to optionsMap[SettingsUtils.SettingNames.IGNORE_INVALID_BYTECODE     ]!!,
				SettingsUtils.SettingNames.LAMBDA_TO_ANONYMOUS_CLASS    to optionsMap[SettingsUtils.SettingNames.LAMBDA_TO_ANONYMOUS_CLASS   ]!!,
				SettingsUtils.SettingNames.LITERALS_AS_IS               to optionsMap[SettingsUtils.SettingNames.LITERALS_AS_IS              ]!!,
				SettingsUtils.SettingNames.MAX_PROCESSING_METHOD        to optionsMap[SettingsUtils.SettingNames.MAX_PROCESSING_METHOD       ]!!,
				SettingsUtils.SettingNames.NO_EXCEPTIONS_RETURN         to optionsMap[SettingsUtils.SettingNames.NO_EXCEPTIONS_RETURN        ]!!,
				SettingsUtils.SettingNames.REMOVE_BRIDGE                to optionsMap[SettingsUtils.SettingNames.REMOVE_BRIDGE               ]!!,
				SettingsUtils.SettingNames.REMOVE_EMPTY_RANGES          to optionsMap[SettingsUtils.SettingNames.REMOVE_EMPTY_RANGES         ]!!,
				SettingsUtils.SettingNames.REMOVE_GET_CLASS_NEW         to optionsMap[SettingsUtils.SettingNames.REMOVE_GET_CLASS_NEW        ]!!,
				SettingsUtils.SettingNames.REMOVE_SYNTHETIC             to optionsMap[SettingsUtils.SettingNames.REMOVE_SYNTHETIC            ]!!,
				SettingsUtils.SettingNames.RENAME_ENTITIES              to optionsMap[SettingsUtils.SettingNames.RENAME_ENTITIES             ]!!,
				SettingsUtils.SettingNames.SYNTHETIC_NOT_SET            to optionsMap[SettingsUtils.SettingNames.SYNTHETIC_NOT_SET           ]!!,
				SettingsUtils.SettingNames.UNDEFINED_PARAM_TYPE_OBJECT  to optionsMap[SettingsUtils.SettingNames.UNDEFINED_PARAM_TYPE_OBJECT ]!!,
				SettingsUtils.SettingNames.USE_DEBUG_VAR_NAMES          to optionsMap[SettingsUtils.SettingNames.USE_DEBUG_VAR_NAMES         ]!!,
				SettingsUtils.SettingNames.USE_METHOD_PARAMETERS        to optionsMap[SettingsUtils.SettingNames.USE_METHOD_PARAMETERS       ]!!,
				SettingsUtils.SettingNames.VERIFY_ANONYMOUS_CLASSES     to optionsMap[SettingsUtils.SettingNames.VERIFY_ANONYMOUS_CLASSES    ]!!,
				SettingsUtils.SettingNames.NEW_LINE_SEPARATOR           to optionsMap[SettingsUtils.SettingNames.NEW_LINE_SEPARATOR          ]!!,
				SettingsUtils.SettingNames.LOG_LEVEL                    to optionsMap[SettingsUtils.SettingNames.LOG_LEVEL                   ]!!,
				SettingsUtils.SettingNames.INDENT_STRING                to optionsMap[SettingsUtils.SettingNames.INDENT_STRING               ]!!,
				SettingsUtils.SettingNames.BANNER                       to optionsMap[SettingsUtils.SettingNames.BANNER                      ]!!
		)
	}

	override fun createCenterPanel():JComponent {
		return mainJPanel
	}

	override fun getPreferredFocusedComponent():JComponent {
		return getButton( super.getOKAction() )!!
	}

	override fun toString(): String {
		val resultBuilder = StringBuilder()
		optionsMap.forEach { k, v ->
			resultBuilder.append(" -$k=$v")
		}
		return resultBuilder.toString()
	}

	private fun setupJRadioButton( panel:JPanel, key:SettingsUtils.SettingNames, x:Int, y:Int, text:String):JRadioButton {
		val value = optionsMap[key]?:"0"
		val button = JRadioButton(text)
		button.toolTipText = "-$text"
		button.isSelected = value == "1"
		optionsMap[key] = value
		panel.add(button, GridBagConstraints(
				x, y,
				1, 1,
				1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE,
				Insets(0, 0, 0, 0),
				0, 0))
		button.addChangeListener { event ->
			val sourceJRadioButton = event.source as JRadioButton
			if(sourceJRadioButton == attach){
				attachOption = sourceJRadioButton.isSelected
			} else {
				val newValue = if (sourceJRadioButton.isSelected) "1" else "0"
				optionsMap[key] = newValue
				storedSettings.setProperty(key, newValue)
			}
		}
		return button
	}

	private fun setupJTextField( panel:JPanel, key:SettingsUtils.SettingNames, y:Int, text:String):JTextField {
		val value = optionsMap[key]
		val defaultValue = if ( value is String ) value else throw Exception("POPUP Error: $key value is not a String")
		val label = JLabel(text)
		label.toolTipText = "-$key"
		panel.add(label, GridBagConstraints(
				0, y,
				1, 1,
				1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE,
				Insets(0, 0, 0, 0),
				0, 0))
		val textField = JTextField(defaultValue)
		panel.add(textField, GridBagConstraints(
				1, y,
				1, 1,
				1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE,
				Insets(0, 0, 0, 0),
				0, 0))
		textField.addPropertyChangeListener { event ->
			val sourceJTextField = event.source as JTextField
			val textStr = sourceJTextField.text?:""
			optionsMap[key] = textStr
			storedSettings.setProperty(key, textStr)

		}
		return textField
	}

	private fun setupJTextArea( panel:JPanel, key:SettingsUtils.SettingNames, y:Int, text:String):JTextArea {
		val value = optionsMap[key]
		val defaultValue = if ( value is String ) value else throw Exception("POPUP Error: $key value is not a String")
		val label = JLabel(text)
		label.toolTipText = "-$key"
		panel.add(label, GridBagConstraints(
				0, y,
				1, 1,
				1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE,
				Insets(0, 0, 0, 0),
				0, 0))
		val textArea = JTextArea(defaultValue)
		panel.add(textArea, GridBagConstraints(
				1, y,
				1, 1,
				1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				Insets(0, 0, 0, 0),
				0, 0))
		textArea.addPropertyChangeListener { event ->
			val sourceJTextField = event.source as JTextArea
			val textStr = sourceJTextField.text?:""
			optionsMap[key] = textStr
			storedSettings.setProperty(key, textStr)
		}
		return textArea
	}

	private fun setupComboBox(panel:JPanel, key:SettingsUtils.SettingNames, y:Int, selections:Array<String>, text:String):ComboBox<String> {
		val value = optionsMap[key]
		val defaultValue = if ( value is String ) value else throw Exception("POPUP Error: $key value is not a String")
		if(defaultValue !in selections) throw Exception("POPUP Error: $key value is not in selection values")
		val label = JLabel(text)
		label.toolTipText = "-$key"
		panel.add(label, GridBagConstraints(
				0, y,
				1, 1,
				1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE,
				Insets(0, 0, 0, 0),
				0, 0))
		val combo = ComboBox(selections)
		combo.selectedItem = defaultValue
		panel.add(combo, GridBagConstraints(
				1, y,
				1, 1,
				1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				Insets(0, 0, 0, 0),
				0, 0))
		combo.addActionListener {
			val newValue = combo.selectedItem.toString()
			optionsMap[key] = newValue
			storedSettings.setProperty(key, newValue)
		}
		return combo
	}

	private fun setupFileSelctor( project:Project, panel:JPanel, key:SettingsUtils.SettingNames, y:Int, titleText:String, descrText:String, saveSelectedDir: (String) -> Unit ):TextFieldWithBrowseButton {
		val label = JLabel(titleText)
		panel.add(label, GridBagConstraints(
				0, y,
				1, 1,
				1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE,
				Insets(0, 0, 0, 0),
				0, 0))
		val folderDescriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor()
		folderDescriptor.description = descrText
		val dirTextField = TextFieldWithBrowseButton()
		dirTextField.text = storedSettings.getProperty(key, "")
		dirTextField.addBrowseFolderListener(null, null, project, folderDescriptor, TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT )
		//dirTextField.addBrowseFolderListener( TextBrowseFolderListener(folderDescriptor, project) )
		panel.add(dirTextField, GridBagConstraints(
				1, y,
				1, 1,
				1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				Insets(0, 0, 0, 0),
				0, 0))
		return dirTextField
	}

	init {
		init()
		//optionsMap[SettingsUtils.SettingNames.DUMP_ORIGINAL_LINES]          = storedSettings.getProperty( SettingsUtils.SettingNames.DUMP_ORIGINAL_LINES,          "0" )
		//optionsMap[SettingsUtils.SettingNames.UNIT_TEST_MODE]               = storedSettings.getProperty( SettingsUtils.SettingNames.UNIT_TEST_MODE,               "0" )
		//optionsMap[SettingsUtils.SettingNames.USER_RENAMER_CLASS]           = storedSettings.getProperty( SettingsUtils.SettingNames.USER_RENAMER_CLASS,           "" )
		optionsMap[SettingsUtils.SettingNames.ASCII_STRING_CHARACTERS]      = storedSettings.getProperty( SettingsUtils.SettingNames.ASCII_STRING_CHARACTERS,      "0" )
		optionsMap[SettingsUtils.SettingNames.BOOLEAN_TRUE_ONE]             = storedSettings.getProperty( SettingsUtils.SettingNames.BOOLEAN_TRUE_ONE,             "1" )
		optionsMap[SettingsUtils.SettingNames.BYTECODE_SOURCE_MAPPING]      = storedSettings.getProperty( SettingsUtils.SettingNames.BYTECODE_SOURCE_MAPPING,      "0" )
		optionsMap[SettingsUtils.SettingNames.DECOMPILE_ASSERTIONS]         = storedSettings.getProperty( SettingsUtils.SettingNames.DECOMPILE_ASSERTIONS,         "1" )
		optionsMap[SettingsUtils.SettingNames.DECOMPILE_CLASS_1_4]          = storedSettings.getProperty( SettingsUtils.SettingNames.DECOMPILE_CLASS_1_4,          "1" )
		optionsMap[SettingsUtils.SettingNames.DECOMPILE_ENUM]               = storedSettings.getProperty( SettingsUtils.SettingNames.DECOMPILE_ENUM,               "1" )
		optionsMap[SettingsUtils.SettingNames.DECOMPILE_GENERIC_SIGNATURES] = storedSettings.getProperty( SettingsUtils.SettingNames.DECOMPILE_GENERIC_SIGNATURES, "0" )
		optionsMap[SettingsUtils.SettingNames.DECOMPILE_INNER]              = storedSettings.getProperty( SettingsUtils.SettingNames.DECOMPILE_INNER,              "1" )
		optionsMap[SettingsUtils.SettingNames.ENSURE_SYNCHRONIZED_MONITOR]  = storedSettings.getProperty( SettingsUtils.SettingNames.ENSURE_SYNCHRONIZED_MONITOR,  "1" )
		optionsMap[SettingsUtils.SettingNames.FINALLY_DEINLINE]             = storedSettings.getProperty( SettingsUtils.SettingNames.FINALLY_DEINLINE,             "1" )
		optionsMap[SettingsUtils.SettingNames.HIDE_DEFAULT_CONSTRUCTOR]     = storedSettings.getProperty( SettingsUtils.SettingNames.HIDE_DEFAULT_CONSTRUCTOR,     "1" )
		optionsMap[SettingsUtils.SettingNames.HIDE_EMPTY_SUPER]             = storedSettings.getProperty( SettingsUtils.SettingNames.HIDE_EMPTY_SUPER,             "1" )
		optionsMap[SettingsUtils.SettingNames.IDEA_NOT_NULL_ANNOTATION]     = storedSettings.getProperty( SettingsUtils.SettingNames.IDEA_NOT_NULL_ANNOTATION,     "1" )
		optionsMap[SettingsUtils.SettingNames.IGNORE_INVALID_BYTECODE]      = storedSettings.getProperty( SettingsUtils.SettingNames.IGNORE_INVALID_BYTECODE,      "0" )
		optionsMap[SettingsUtils.SettingNames.LAMBDA_TO_ANONYMOUS_CLASS]    = storedSettings.getProperty( SettingsUtils.SettingNames.LAMBDA_TO_ANONYMOUS_CLASS,    "0" )
		optionsMap[SettingsUtils.SettingNames.LITERALS_AS_IS]               = storedSettings.getProperty( SettingsUtils.SettingNames.LITERALS_AS_IS,               "0" )
		optionsMap[SettingsUtils.SettingNames.MAX_PROCESSING_METHOD]        = storedSettings.getProperty( SettingsUtils.SettingNames.MAX_PROCESSING_METHOD,        "0" )
		optionsMap[SettingsUtils.SettingNames.NO_EXCEPTIONS_RETURN]         = storedSettings.getProperty( SettingsUtils.SettingNames.NO_EXCEPTIONS_RETURN,         "1" )
		optionsMap[SettingsUtils.SettingNames.REMOVE_BRIDGE]                = storedSettings.getProperty( SettingsUtils.SettingNames.REMOVE_BRIDGE,                "1" )
		optionsMap[SettingsUtils.SettingNames.REMOVE_EMPTY_RANGES]          = storedSettings.getProperty( SettingsUtils.SettingNames.REMOVE_EMPTY_RANGES,          "1" )
		optionsMap[SettingsUtils.SettingNames.REMOVE_GET_CLASS_NEW]         = storedSettings.getProperty( SettingsUtils.SettingNames.REMOVE_GET_CLASS_NEW,         "1" )
		optionsMap[SettingsUtils.SettingNames.REMOVE_SYNTHETIC]             = storedSettings.getProperty( SettingsUtils.SettingNames.REMOVE_SYNTHETIC,             "0" )
		optionsMap[SettingsUtils.SettingNames.RENAME_ENTITIES]              = storedSettings.getProperty( SettingsUtils.SettingNames.RENAME_ENTITIES,              "0" )
		optionsMap[SettingsUtils.SettingNames.SYNTHETIC_NOT_SET]            = storedSettings.getProperty( SettingsUtils.SettingNames.SYNTHETIC_NOT_SET,            "0" )
		optionsMap[SettingsUtils.SettingNames.UNDEFINED_PARAM_TYPE_OBJECT]  = storedSettings.getProperty( SettingsUtils.SettingNames.UNDEFINED_PARAM_TYPE_OBJECT,  "1" )
		optionsMap[SettingsUtils.SettingNames.USE_DEBUG_VAR_NAMES]          = storedSettings.getProperty( SettingsUtils.SettingNames.USE_DEBUG_VAR_NAMES,          "1" )
		optionsMap[SettingsUtils.SettingNames.USE_METHOD_PARAMETERS]        = storedSettings.getProperty( SettingsUtils.SettingNames.USE_METHOD_PARAMETERS,        "1" )
		optionsMap[SettingsUtils.SettingNames.VERIFY_ANONYMOUS_CLASSES]     = storedSettings.getProperty( SettingsUtils.SettingNames.VERIFY_ANONYMOUS_CLASSES,     "0" )
		optionsMap[SettingsUtils.SettingNames.NEW_LINE_SEPARATOR]           = storedSettings.getProperty( SettingsUtils.SettingNames.NEW_LINE_SEPARATOR, if (InterpreterUtil.IS_WINDOWS) "0" else "1" )
		optionsMap[SettingsUtils.SettingNames.LOG_LEVEL]                    = storedSettings.getProperty( SettingsUtils.SettingNames.LOG_LEVEL,                    IFernflowerLogger.Severity.INFO.name )
		optionsMap[SettingsUtils.SettingNames.INDENT_STRING]                = storedSettings.getProperty( SettingsUtils.SettingNames.INDENT_STRING,                "   " )
		optionsMap[SettingsUtils.SettingNames.BANNER]                       = storedSettings.getProperty( SettingsUtils.SettingNames.BANNER,                       "//\n// Source code recreated from a .class file by IntelliJ IDEA\n// (powered by Fernflower decompiler)\n//\n\n" )

		title = "Fernflower Options"
		val panel1 = JPanel()
		panel1.layout = GridBagLayout()
		mainJPanel = JPanel()
		mainJPanel.layout = GridBagLayout()
		panel1.add(mainJPanel, GridBagConstraints(
				0, 0,
				1, 1,
				0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE,
				Insets(0, 0, 0, 0),
				0, 0))


		var gridY0 = 0
		var gridY1 = 0
		rbr = setupJRadioButton( mainJPanel, SettingsUtils.SettingNames.REMOVE_BRIDGE               , 0, gridY0++, "rbr (1): hide bridge methods")
		din = setupJRadioButton( mainJPanel, SettingsUtils.SettingNames.DECOMPILE_INNER             , 0, gridY0++, "din (1): decompile inner classes" )
		dc4 = setupJRadioButton( mainJPanel, SettingsUtils.SettingNames.DECOMPILE_CLASS_1_4         , 0, gridY0++, "dc4 (1): collapse 1.4 class references" )
		das = setupJRadioButton( mainJPanel, SettingsUtils.SettingNames.DECOMPILE_ASSERTIONS        , 0, gridY0++, "das (1): decompile assertions" )
		hes = setupJRadioButton( mainJPanel, SettingsUtils.SettingNames.HIDE_EMPTY_SUPER            , 0, gridY0++, "hes (1): hide empty super invocation" )
		hdc = setupJRadioButton( mainJPanel, SettingsUtils.SettingNames.HIDE_DEFAULT_CONSTRUCTOR    , 0, gridY0++, "hdc (1): hide empty default constructor" )
		ner = setupJRadioButton( mainJPanel, SettingsUtils.SettingNames.NO_EXCEPTIONS_RETURN        , 0, gridY0++, "ner (1): assume return not throwing exceptions" )
		esm = setupJRadioButton( mainJPanel, SettingsUtils.SettingNames.ENSURE_SYNCHRONIZED_MONITOR , 0, gridY0++, "esm: Ensure synchronized monitor" )
		den = setupJRadioButton( mainJPanel, SettingsUtils.SettingNames.DECOMPILE_ENUM              , 0, gridY0++, "den (1): decompile enumerations" )
		rgn = setupJRadioButton( mainJPanel, SettingsUtils.SettingNames.REMOVE_GET_CLASS_NEW        , 0, gridY0++, "rgn (1): remove getClass() invocation, when it is part of a qualified new statement" )
		bto = setupJRadioButton( mainJPanel, SettingsUtils.SettingNames.BOOLEAN_TRUE_ONE            , 0, gridY0++, "bto (1): interpret int 1 as boolean true (workaround to a compiler bug)" )
		uto = setupJRadioButton( mainJPanel, SettingsUtils.SettingNames.UNDEFINED_PARAM_TYPE_OBJECT , 0, gridY0++, "uto (1): consider nameless types as java.lang.Object (workaround to a compiler architecture flaw)" )
		udv = setupJRadioButton( mainJPanel, SettingsUtils.SettingNames.USE_DEBUG_VAR_NAMES         , 0, gridY0++, "udv (1): reconstruct variable names from debug information, if present" )
		rer = setupJRadioButton( mainJPanel, SettingsUtils.SettingNames.REMOVE_EMPTY_RANGES         , 0, gridY0++, "rer (1): remove empty exception ranges" )
		fdi = setupJRadioButton( mainJPanel, SettingsUtils.SettingNames.FINALLY_DEINLINE            , 0, gridY0++, "fdi (1): de-inline finally structures" )
		inn = setupJRadioButton( mainJPanel, SettingsUtils.SettingNames.IDEA_NOT_NULL_ANNOTATION    , 0, gridY0++, "inn (1): check for IntelliJ IDEA-specific @NotNull annotation and remove inserted code if found" )

		rsy = setupJRadioButton( mainJPanel, SettingsUtils.SettingNames.REMOVE_SYNTHETIC            , 1, gridY1++, "rsy (0): hide synthetic class members" )
		dgs = setupJRadioButton( mainJPanel, SettingsUtils.SettingNames.DECOMPILE_GENERIC_SIGNATURES, 1, gridY1++, "dgs (0): decompile generic signatures" )
		lit = setupJRadioButton( mainJPanel, SettingsUtils.SettingNames.LITERALS_AS_IS              , 1, gridY1++, "lit (0): output numeric literals \"as-is\"" )
		asc = setupJRadioButton( mainJPanel, SettingsUtils.SettingNames.ASCII_STRING_CHARACTERS     , 1, gridY1++, "asc (0): encode non-ASCII characters in string and character literals as Unicode escapes" )
		nss = setupJRadioButton( mainJPanel, SettingsUtils.SettingNames.SYNTHETIC_NOT_SET           , 1, gridY1++, "nns (0): allow for not set synthetic attribute (workaround to a compiler bug)" )
		lac = setupJRadioButton( mainJPanel, SettingsUtils.SettingNames.LAMBDA_TO_ANONYMOUS_CLASS   , 1, gridY1++, "lac (0): decompile lambda expressions to anonymous classes" )
		nls = setupJRadioButton( mainJPanel, SettingsUtils.SettingNames.NEW_LINE_SEPARATOR          , 1, gridY1++, "nls (0): define new line character to be used for output. 0 - '\\r\\n' (Windows), 1 - '\\n' (Unix), default is OS-dependent" )
		ump = setupJRadioButton( mainJPanel, SettingsUtils.SettingNames.USE_METHOD_PARAMETERS       , 1, gridY1++, "ump: Use Method Parameters" )
		bsm = setupJRadioButton( mainJPanel, SettingsUtils.SettingNames.BYTECODE_SOURCE_MAPPING     , 1, gridY1++, "bsm: Bytecode source mapping" )
		iib = setupJRadioButton( mainJPanel, SettingsUtils.SettingNames.IGNORE_INVALID_BYTECODE     , 1, gridY1++, "iib: Ignore Invalid Bytecod" )
		vac = setupJRadioButton( mainJPanel, SettingsUtils.SettingNames.VERIFY_ANONYMOUS_CLASSES    , 1, gridY1++, "vac: Verify Anonymous Classes" )
		ren = setupJRadioButton( mainJPanel, SettingsUtils.SettingNames.RENAME_ENTITIES             , 1, gridY1++, "ren (0): rename ambiguous (resp. obfuscated) classes and class elements" )

		var gridY2 = max( gridY0, gridY1 )
		//urc = setupJTextField(   mainJPanel, SettingsUtils.SettingNames.USER_RENAMER_CLASS         , gridY2++, "urc (-): full name of a user-supplied class implementing IIdentifierRenamer interface. (currently not implemented in this plugin)")
		mpm = setupJTextField(   mainJPanel, SettingsUtils.SettingNames.MAX_PROCESSING_METHOD      , gridY2++, "mpm (0): maximum allowed processing time per decompiled method, in seconds. 0 means no upper limit")
		ind = setupJTextField(   mainJPanel, SettingsUtils.SettingNames.INDENT_STRING              , gridY2++, "ind: indentation string (default is 3 spaces)")
		ban = setupJTextArea(    mainJPanel, SettingsUtils.SettingNames.BANNER                     , gridY2++, "ban: set Banner for each source file" )

		val selection = Array(IFernflowerLogger.Severity.values().size) { index -> IFernflowerLogger.Severity.values()[index].name }
		log                 = setupComboBox(            mainJPanel, SettingsUtils.SettingNames.LOG_LEVEL                  , gridY2++, selection, "log (INFO): a logging level, possible values are TRACE, INFO, WARN, ERROR" )

		attach              = setupJRadioButton(        mainJPanel, SettingsUtils.SettingNames.ATTACH_SOURCE_JAR, 0, gridY2++, "Attache source to jar")

		decompiledJarFolder = setupFileSelctor(project, mainJPanel, SettingsUtils.SettingNames.DECOMPILED_JAR_DIR, gridY2++, "Select output folder for generated source JAR", "The xxx-sources.jar will be placed in this director") { selectedFolder:String -> decompiledJarFolderString = selectedFolder }
		decompiledSrcDir    = setupFileSelctor(project, mainJPanel, SettingsUtils.SettingNames.DECOMPILED_SRC_DIR, gridY2++, "Select output folder for source tree",          "The decompiled will be placed in this director"     ) { selectedFolder:String -> decompiledSrcFolderString = selectedFolder }
		logger.info("mexGridY0: $gridY0")
		logger.info("mexGridY1: $gridY1")
		logger.info("mexGridY2: $gridY2")
		contentPanel.add(mainJPanel)
	}

}
