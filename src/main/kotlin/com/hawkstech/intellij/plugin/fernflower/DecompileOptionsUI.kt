package com.hawkstech.intellij.plugin.fernflower

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogWrapper
import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger
import org.jetbrains.java.decompiler.main.extern.IFernflowerPreferences
import org.jetbrains.java.decompiler.main.extern.IFernflowerPreferences.*
import org.jetbrains.java.decompiler.util.InterpreterUtil
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.util.HashMap
import javax.swing.*
import kotlin.math.max


class DecompileOptionsUI(project:Project):DialogWrapper(project) {
	private val logger = Logger.getInstance(DecompileOptionsUI::class.java)
	private val optionsMap = HashMap<String, Any>()
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

	fun getAttachOption():Boolean {
		return attachOption
	}

	fun getParsedOptions():Map<String, Any> {
		var options: Map<String, Any> = mapOf(
				ASCII_STRING_CHARACTERS      to optionsMap[ASCII_STRING_CHARACTERS     ]!!,
				BOOLEAN_TRUE_ONE             to optionsMap[BOOLEAN_TRUE_ONE            ]!!,
				BYTECODE_SOURCE_MAPPING      to optionsMap[BYTECODE_SOURCE_MAPPING     ]!!,
				DECOMPILE_ASSERTIONS         to optionsMap[DECOMPILE_ASSERTIONS        ]!!,
				DECOMPILE_CLASS_1_4          to optionsMap[DECOMPILE_CLASS_1_4         ]!!,
				DECOMPILE_ENUM               to optionsMap[DECOMPILE_ENUM              ]!!,
				DECOMPILE_GENERIC_SIGNATURES to optionsMap[DECOMPILE_GENERIC_SIGNATURES]!!,
				DECOMPILE_INNER              to optionsMap[DECOMPILE_INNER             ]!!,
				ENSURE_SYNCHRONIZED_MONITOR  to optionsMap[ENSURE_SYNCHRONIZED_MONITOR ]!!,
				FINALLY_DEINLINE             to optionsMap[FINALLY_DEINLINE            ]!!,
				HIDE_DEFAULT_CONSTRUCTOR     to optionsMap[HIDE_DEFAULT_CONSTRUCTOR    ]!!,
				HIDE_EMPTY_SUPER             to optionsMap[HIDE_EMPTY_SUPER            ]!!,
				IDEA_NOT_NULL_ANNOTATION     to optionsMap[IDEA_NOT_NULL_ANNOTATION    ]!!,
				IGNORE_INVALID_BYTECODE      to optionsMap[IGNORE_INVALID_BYTECODE     ]!!,
				LAMBDA_TO_ANONYMOUS_CLASS    to optionsMap[LAMBDA_TO_ANONYMOUS_CLASS   ]!!,
				LITERALS_AS_IS               to optionsMap[LITERALS_AS_IS              ]!!,
				MAX_PROCESSING_METHOD        to optionsMap[MAX_PROCESSING_METHOD       ]!!,
				NO_EXCEPTIONS_RETURN         to optionsMap[NO_EXCEPTIONS_RETURN        ]!!,
				REMOVE_BRIDGE                to optionsMap[REMOVE_BRIDGE               ]!!,
				REMOVE_EMPTY_RANGES          to optionsMap[REMOVE_EMPTY_RANGES         ]!!,
				REMOVE_GET_CLASS_NEW         to optionsMap[REMOVE_GET_CLASS_NEW        ]!!,
				REMOVE_SYNTHETIC             to optionsMap[REMOVE_SYNTHETIC            ]!!,
				RENAME_ENTITIES              to optionsMap[RENAME_ENTITIES             ]!!,
				SYNTHETIC_NOT_SET            to optionsMap[SYNTHETIC_NOT_SET           ]!!,
				UNDEFINED_PARAM_TYPE_OBJECT  to optionsMap[UNDEFINED_PARAM_TYPE_OBJECT ]!!,
				USE_DEBUG_VAR_NAMES          to optionsMap[USE_DEBUG_VAR_NAMES         ]!!,
				USE_METHOD_PARAMETERS        to optionsMap[USE_METHOD_PARAMETERS       ]!!,
				VERIFY_ANONYMOUS_CLASSES     to optionsMap[VERIFY_ANONYMOUS_CLASSES    ]!!,
				NEW_LINE_SEPARATOR           to optionsMap[NEW_LINE_SEPARATOR          ]!!,
				LOG_LEVEL                    to optionsMap[LOG_LEVEL                   ]!!,
				INDENT_STRING                to optionsMap[INDENT_STRING               ]!!,
				BANNER                       to optionsMap[BANNER                      ]!!
		)
		return options
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

	private fun setupJRadioButton( panel:JPanel, key:String, x:Int, y:Int, text:String):JRadioButton {
		val value = optionsMap[key]?:"0"
		val defaultValue = if ( value is String ) value else throw Exception("Decompiler option Error: $key value is not a String")
		val button = JRadioButton(text)
		button.toolTipText = "-$text"
		button.isSelected = defaultValue == "1"
		optionsMap[key] = defaultValue
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
				optionsMap[key] = if (sourceJRadioButton.isSelected) "1" else "0"
			}
		}
		return button
	}

	private fun setupJTextField( panel:JPanel, key:String, y:Int, text:String):JTextField {
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
		}
		return textField
	}

	private fun setupJTextArea( panel:JPanel, key:String, y:Int, text:String):JTextArea {
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
				GridBagConstraints.WEST, GridBagConstraints.NONE,
				Insets(0, 0, 0, 0),
				0, 0))
		textArea.addPropertyChangeListener { event ->
			val sourceJTextField = event.source as JTextArea
			val textStr = sourceJTextField.text?:""
			optionsMap[key] = textStr
		}
		return textArea
	}
	private fun setupComboBox(panel:JPanel, key:String, y:Int, selections:Array<String>, text:String):ComboBox<String> {
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
				GridBagConstraints.WEST, GridBagConstraints.NONE,
				Insets(0, 0, 0, 0),
				0, 0))
		combo.addActionListener {
			optionsMap[key] = combo.selectedItem.toString()
		}
		return combo
	}

	init {
		init()
		optionsMap[ASCII_STRING_CHARACTERS]      = "0"
		optionsMap[BOOLEAN_TRUE_ONE]             = "1"
		optionsMap[BYTECODE_SOURCE_MAPPING]      = "0"
		optionsMap[DECOMPILE_ASSERTIONS]         = "1"
		optionsMap[DECOMPILE_CLASS_1_4]          = "1"
		optionsMap[DECOMPILE_ENUM]               = "1"
		optionsMap[DECOMPILE_GENERIC_SIGNATURES] = "0"
		optionsMap[DECOMPILE_INNER]              = "1"
		//options[DUMP_ORIGINAL_LINES]          = "0"
		optionsMap[ENSURE_SYNCHRONIZED_MONITOR]  = "1"
		optionsMap[FINALLY_DEINLINE]             = "1"
		optionsMap[HIDE_DEFAULT_CONSTRUCTOR]     = "1"
		optionsMap[HIDE_EMPTY_SUPER]             = "1"
		optionsMap[IDEA_NOT_NULL_ANNOTATION]     = "1"
		optionsMap[IGNORE_INVALID_BYTECODE]      = "0"
		optionsMap[LAMBDA_TO_ANONYMOUS_CLASS]    = "0"
		optionsMap[LITERALS_AS_IS]               = "0"
		optionsMap[MAX_PROCESSING_METHOD]        = "0"
		optionsMap[NO_EXCEPTIONS_RETURN]         = "1"
		optionsMap[REMOVE_BRIDGE]                = "1"
		optionsMap[REMOVE_EMPTY_RANGES]          = "1"
		optionsMap[REMOVE_GET_CLASS_NEW]         = "1"
		optionsMap[REMOVE_SYNTHETIC]             = "0"
		optionsMap[RENAME_ENTITIES]              = "0"
		optionsMap[SYNTHETIC_NOT_SET]            = "0"
		optionsMap[UNDEFINED_PARAM_TYPE_OBJECT]  = "1"
		//options[UNIT_TEST_MODE]               = "0"
		optionsMap[USE_DEBUG_VAR_NAMES]          = "1"
		optionsMap[USE_METHOD_PARAMETERS]        = "1"
		optionsMap[VERIFY_ANONYMOUS_CLASSES]     = "0"
		optionsMap[NEW_LINE_SEPARATOR]           = if (InterpreterUtil.IS_WINDOWS) "0" else "1"
		optionsMap[LOG_LEVEL]                    = IFernflowerLogger.Severity.INFO.name
		optionsMap[INDENT_STRING]                = "   "
		//options[USER_RENAMER_CLASS]           = ""
		optionsMap[BANNER]                       = "//\n// Source code recreated from a .class file by IntelliJ IDEA\n// (powered by Fernflower decompiler)\n//\n\n"

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
		rbr = setupJRadioButton( mainJPanel, IFernflowerPreferences.REMOVE_BRIDGE               , 0, gridY0++, "rbr (1): hide bridge methods")
		din = setupJRadioButton( mainJPanel, IFernflowerPreferences.DECOMPILE_INNER             , 0, gridY0++, "din (1): decompile inner classes" )
		dc4 = setupJRadioButton( mainJPanel, IFernflowerPreferences.DECOMPILE_CLASS_1_4         , 0, gridY0++, "dc4 (1): collapse 1.4 class references" )
		das = setupJRadioButton( mainJPanel, IFernflowerPreferences.DECOMPILE_ASSERTIONS        , 0, gridY0++, "das (1): decompile assertions" )
		hes = setupJRadioButton( mainJPanel, IFernflowerPreferences.HIDE_EMPTY_SUPER            , 0, gridY0++, "hes (1): hide empty super invocation" )
		hdc = setupJRadioButton( mainJPanel, IFernflowerPreferences.HIDE_DEFAULT_CONSTRUCTOR    , 0, gridY0++, "hdc (1): hide empty default constructor" )
		ner = setupJRadioButton( mainJPanel, IFernflowerPreferences.NO_EXCEPTIONS_RETURN        , 0, gridY0++, "ner (1): assume return not throwing exceptions" )
		esm = setupJRadioButton( mainJPanel, IFernflowerPreferences.ENSURE_SYNCHRONIZED_MONITOR , 0, gridY0++, "esm: Ensure synchronized monitor" )
		den = setupJRadioButton( mainJPanel, IFernflowerPreferences.DECOMPILE_ENUM              , 0, gridY0++, "den (1): decompile enumerations" )
		rgn = setupJRadioButton( mainJPanel, IFernflowerPreferences.REMOVE_GET_CLASS_NEW        , 0, gridY0++, "rgn (1): remove getClass() invocation, when it is part of a qualified new statement" )
		bto = setupJRadioButton( mainJPanel, IFernflowerPreferences.BOOLEAN_TRUE_ONE            , 0, gridY0++, "bto (1): interpret int 1 as boolean true (workaround to a compiler bug)" )
		uto = setupJRadioButton( mainJPanel, IFernflowerPreferences.UNDEFINED_PARAM_TYPE_OBJECT , 0, gridY0++, "uto (1): consider nameless types as java.lang.Object (workaround to a compiler architecture flaw)" )
		udv = setupJRadioButton( mainJPanel, IFernflowerPreferences.USE_DEBUG_VAR_NAMES         , 0, gridY0++, "udv (1): reconstruct variable names from debug information, if present" )
		rer = setupJRadioButton( mainJPanel, IFernflowerPreferences.REMOVE_EMPTY_RANGES         , 0, gridY0++, "rer (1): remove empty exception ranges" )
		fdi = setupJRadioButton( mainJPanel, IFernflowerPreferences.FINALLY_DEINLINE            , 0, gridY0++, "fdi (1): de-inline finally structures" )
		inn = setupJRadioButton( mainJPanel, IFernflowerPreferences.IDEA_NOT_NULL_ANNOTATION    , 0, gridY0++, "inn (1): check for IntelliJ IDEA-specific @NotNull annotation and remove inserted code if found" )

		rsy = setupJRadioButton( mainJPanel, IFernflowerPreferences.REMOVE_SYNTHETIC            , 1, gridY1++, "rsy (0): hide synthetic class members" )
		dgs = setupJRadioButton( mainJPanel, IFernflowerPreferences.DECOMPILE_GENERIC_SIGNATURES, 1, gridY1++, "dgs (0): decompile generic signatures" )
		lit = setupJRadioButton( mainJPanel, IFernflowerPreferences.LITERALS_AS_IS              , 1, gridY1++, "lit (0): output numeric literals \"as-is\"" )
		asc = setupJRadioButton( mainJPanel, IFernflowerPreferences.ASCII_STRING_CHARACTERS     , 1, gridY1++, "asc (0): encode non-ASCII characters in string and character literals as Unicode escapes" )
		nss = setupJRadioButton( mainJPanel, IFernflowerPreferences.SYNTHETIC_NOT_SET           , 1, gridY1++, "nns (0): allow for not set synthetic attribute (workaround to a compiler bug)" )
		lac = setupJRadioButton( mainJPanel, IFernflowerPreferences.LAMBDA_TO_ANONYMOUS_CLASS   , 1, gridY1++, "lac (0): decompile lambda expressions to anonymous classes" )
		nls = setupJRadioButton( mainJPanel, IFernflowerPreferences.NEW_LINE_SEPARATOR          , 1, gridY1++, "nls (0): define new line character to be used for output. 0 - '\\r\\n' (Windows), 1 - '\\n' (Unix), default is OS-dependent" )
		ump = setupJRadioButton( mainJPanel, IFernflowerPreferences.USE_METHOD_PARAMETERS       , 1, gridY1++, "ump: Use Method Parameters" )
		bsm = setupJRadioButton( mainJPanel, IFernflowerPreferences.BYTECODE_SOURCE_MAPPING     , 1, gridY1++, "bsm: Bytecode source mapping" )
		iib = setupJRadioButton( mainJPanel, IFernflowerPreferences.IGNORE_INVALID_BYTECODE     , 1, gridY1++, "iib: Ignore Invalid Bytecod" )
		vac = setupJRadioButton( mainJPanel, IFernflowerPreferences.VERIFY_ANONYMOUS_CLASSES    , 1, gridY1++, "vac: Verify Anonymous Classes" )
		ren = setupJRadioButton( mainJPanel, IFernflowerPreferences.RENAME_ENTITIES             , 1, gridY1++, "ren (0): rename ambiguous (resp. obfuscated) classes and class elements" )

		var gridY2 = max( gridY0, gridY1 )
		//urc = setupJTextField(   mainJPanel, IFernflowerPreferences.USER_RENAMER_CLASS         , gridY2++, "urc (-): full name of a user-supplied class implementing IIdentifierRenamer interface. (currently not implemented in this plugin)")
		mpm = setupJTextField(   mainJPanel, IFernflowerPreferences.MAX_PROCESSING_METHOD      , gridY2++, "mpm (0): maximum allowed processing time per decompiled method, in seconds. 0 means no upper limit")
		ind = setupJTextField(   mainJPanel, IFernflowerPreferences.INDENT_STRING              , gridY2++, "ind: indentation string (default is 3 spaces)")
		ban = setupJTextArea(    mainJPanel, IFernflowerPreferences.BANNER                     , gridY2++, "ban: set Banner for each source file" )

		val selection = Array(IFernflowerLogger.Severity.values().size) { index -> IFernflowerLogger.Severity.values()[index].name }
		log = setupComboBox(     mainJPanel, IFernflowerPreferences.LOG_LEVEL                  , gridY2++, selection, "log (INFO): a logging level, possible values are TRACE, INFO, WARN, ERROR" )
		attach = setupJRadioButton( mainJPanel, "attach", 0, gridY2++, "Attache source to jar")
		logger.info("mexGridY0: $gridY0")
		logger.info("mexGridY1: $gridY1")
		logger.info("mexGridY2: $gridY2")
		contentPanel.add(mainJPanel)
	}

}
