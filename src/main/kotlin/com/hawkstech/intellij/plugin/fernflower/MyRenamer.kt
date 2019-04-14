// Copyright 2000-2017 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.hawkstech.intellij.plugin.fernflower

import org.jetbrains.java.decompiler.code.CodeConstants
import org.jetbrains.java.decompiler.main.extern.IIdentifierRenamer

import java.util.Arrays
import java.util.HashSet
import java.util.Locale

class MyRenamer : IIdentifierRenamer {

	private var classCounter = 0
	private var fieldCounter = 0
	private var methodCounter = 0
	private val setNonStandardClassNames = HashSet<String>()

	override fun toBeRenamed(elementType: IIdentifierRenamer.Type, className: String?, element: String?, descriptor: String?): Boolean {
		val value = if (elementType == IIdentifierRenamer.Type.ELEMENT_CLASS) className else element
		return value == null ||
				value.length <= 2 ||
				!isValidIdentifier(elementType == IIdentifierRenamer.Type.ELEMENT_METHOD, value) ||
				KEYWORDS.contains(value) ||
				elementType == IIdentifierRenamer.Type.ELEMENT_CLASS && (RESERVED_WINDOWS_NAMESPACE.contains(value.toLowerCase(Locale.US)) || value.length > 255 - ".class".length)
	}

	// TODO: consider possible conflicts with not renamed classes, fields and methods!
	// We should get all relevant information here.
	override fun getNextClassName(fullName: String, shortName: String?): String {
		if (shortName == null) {
			return "class_${shortName}_${classCounter++}"
		}

		var index = 0
		while (index < shortName.length && Character.isDigit(shortName[index])) {
			index++
		}

		return if (index == 0 || index == shortName.length) {
			"class_${shortName}_${classCounter++}"
		} else {
			val name = shortName.substring(index)
			if (setNonStandardClassNames.contains(name)) {
				"Inner${name}_${classCounter++}"
			} else {
				setNonStandardClassNames.add(name)
				"Inner$name"
			}
		}
	}

	override fun getNextFieldName(className: String, field: String, descriptor: String): String {
		return "field_${field}_${fieldCounter++}"
	}

	override fun getNextMethodName(className: String, method: String, descriptor: String): String {
		return "method_${method}_${methodCounter++}"
	}

	companion object {
		private val KEYWORDS = HashSet(Arrays.asList(
				"abstract", "do", "if", "package", "synchronized", "boolean", "double", "implements", "private", "this", "break", "else", "import",
				"protected", "throw", "byte", "extends", "instanceof", "public", "throws", "case", "false", "int", "return", "transient", "catch",
				"final", "interface", "short", "true", "char", "finally", "long", "static", "try", "class", "float", "native", "strictfp", "void",
				"const", "for", "new", "super", "volatile", "continue", "goto", "null", "switch", "while", "default", "assert", "enum"))
		private val RESERVED_WINDOWS_NAMESPACE = HashSet(Arrays.asList(
				"con", "prn", "aux", "nul",
				"com1", "com2", "com3", "com4", "com5", "com6", "com7", "com8", "com9",
				"lpt1", "lpt2", "lpt3", "lpt4", "lpt5", "lpt6", "lpt7", "lpt8", "lpt9"))

		/**
		 * Return `true` if, and only if identifier passed is compliant to JLS9 section 3.8 AND DOES NOT CONTAINS so-called "ignorable" characters.
		 * Ignorable characters are removed by javac silently during compilation and thus may appear only in specially crafted obfuscated classes.
		 * For more information about "ignorable" characters see [JDK-7144981](https://bugs.openjdk.java.net/browse/JDK-7144981).
		 *
		 * @param identifier Identifier to be checked
		 * @return `true` in case `identifier` passed can be used as an identifier; `false` otherwise.
		 */
		private fun isValidIdentifier(isMethod: Boolean, identifier: String?): Boolean {

			assert(identifier != null) { "Null identifier passed to the isValidIdentifier() method." }
			assert(identifier!!.isNotEmpty()) { "Empty identifier passed to the isValidIdentifier() method." }

			if (isMethod && (identifier == CodeConstants.INIT_NAME || identifier == CodeConstants.CLINIT_NAME)) {
				return true
			}

			if (!Character.isJavaIdentifierStart(identifier[0])) {
				return false
			}

			val chars = identifier.toCharArray()

			for (i in 1 until chars.size) {
				val ch = chars[i]

				if (!Character.isJavaIdentifierPart(ch) || Character.isIdentifierIgnorable(ch)) {
					return false
				}
			}

			return true

		}

		// *****************************************************************************
		// static methods
		// *****************************************************************************

		fun getSimpleClassName(fullName: String): String {
			return fullName.substring(fullName.lastIndexOf('/') + 1)
		}

		fun replaceSimpleClassName(fullName: String, newName: String): String {
			return fullName.substring(0, fullName.lastIndexOf('/') + 1) + newName
		}
	}
}
