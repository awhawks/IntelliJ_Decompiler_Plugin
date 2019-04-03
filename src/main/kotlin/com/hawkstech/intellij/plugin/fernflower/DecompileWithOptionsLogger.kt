// Copyright 2000-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.hawkstech.intellij.plugin.fernflower

import com.intellij.openapi.diagnostic.Logger
import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger
import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger.Severity.*

class DecompileWithOptionsLogger: IFernflowerLogger() {
	private val LOG = Logger.getInstance(DecompileWithOptionsLogger::class.java)
	private var myClass: String? = null

	override fun writeMessage(message: String, severity: IFernflowerLogger.Severity) {
		val text = extendMessage(message)
		when (severity) {
			ERROR -> LOG.warn(text)
			WARN -> LOG.warn(text)
			INFO -> LOG.info(text)
			else -> LOG.debug(text)
		}
	}

	override fun writeMessage(message: String, severity: IFernflowerLogger.Severity, t: Throwable) {
		val text = extendMessage(message)
		when (severity) {
			WARN -> LOG.warn(text, t)
			INFO -> LOG.info(text, t)
			ERROR -> LOG.error(text, t)
			else -> LOG.debug(text, t)
		}
	}

	private fun extendMessage(message: String) = if (myClass != null) "$message [$myClass]" else message

	override fun startReadingClass(className: String) {
		LOG.debug("decompiling class $className")
		myClass = className
	}

	override fun endReadingClass() {
		LOG.debug("... class decompiled")
		myClass = null
	}

	override fun startClass(className: String): Unit = LOG.debug("processing class $className")

	override fun endClass(): Unit = LOG.debug("... class processed")

	override fun startMethod(methodName: String): Unit = LOG.debug("processing method $methodName")

	override fun endMethod(): Unit = LOG.debug("... method processed")

	override fun startWriteClass(className: String): Unit = LOG.debug("writing class $className")

	override fun endWriteClass(): Unit = LOG.debug("... class written")}
