package com.hawkstech.intellij.plugin.fernflower

import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.LabeledComponent
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.uiDesigner.core.GridConstraints
import com.intellij.uiDesigner.core.GridLayoutManager
import java.awt.BorderLayout
import java.awt.Insets
import java.awt.event.ActionListener
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class FolderSelectionForm(project: Project, dialogTitle:String ) : DialogWrapper(project) {
	private var contentPane:JPanel      = JPanel()
	private var descriptionLabel:JLabel = JLabel()
	private var workingDirComponent: LabeledComponent<TextFieldWithBrowseButton> = LabeledComponent()

	val selectedPath: String get() = workingDirComponent.component.text

	init {
		init()
		title = dialogTitle
		contentPane.layout = GridLayoutManager(2, 1, Insets(10, 10, 10, 10), -1, -1)
		val panel1 = JPanel()
		panel1.layout = BorderLayout(0, 0)
		contentPane.add(panel1, GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK or GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK or GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false))
		workingDirComponent = LabeledComponent()
		workingDirComponent.layout = BorderLayout(0, 0)
		workingDirComponent.componentClass = "com.intellij.openapi.ui.TextFieldWithBrowseButton"
		workingDirComponent.isEnabled = true
		workingDirComponent.text = "Select working dir"
		panel1.add(workingDirComponent, BorderLayout.CENTER)
		descriptionLabel.text = "Label"
		contentPane.add(descriptionLabel, GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false))


		title = title
		isOKActionEnabled = false
		descriptionLabel.text = dialogTitle
		workingDirComponent.component = TextFieldWithBrowseButton(ActionListener {
			if (!workingDirComponent.component.text.isEmpty()) {
				isOKActionEnabled = true
			}
		})
		workingDirComponent.component.addBrowseFolderListener(
				"Test Title", "", project,
				FileChooserDescriptor(false, true, false, false, false, false))
		workingDirComponent.component.isEditable = false
		workingDirComponent.component.setTextFieldPreferredWidth(50)
	}

	override fun createCenterPanel():JComponent? {
		return contentPane
	}
}
