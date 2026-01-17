package com.example.rmmobileassistant

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class StepEditorActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private var steps: MutableList<Step> = mutableListOf()
    private var selectedIndex: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        val root = findViewById<View>(R.id.root)
        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        listView = findViewById(R.id.listSteps)

        steps = StepsRepo.loadSteps(this)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_activated_1, displayList())
        listView.adapter = adapter
        listView.choiceMode = ListView.CHOICE_MODE_SINGLE

        listView.setOnItemClickListener { _, _, position, _ ->
            selectedIndex = position
            listView.setItemChecked(position, true)
        }

        findViewById<Button>(R.id.btnAdd).setOnClickListener { addStep() }
        findViewById<Button>(R.id.btnEdit).setOnClickListener { editStep() }
        findViewById<Button>(R.id.btnDel).setOnClickListener { deleteStep() }
        findViewById<Button>(R.id.btnUp).setOnClickListener { moveUp() }
        findViewById<Button>(R.id.btnDown).setOnClickListener { moveDown() }
        findViewById<Button>(R.id.btnSave).setOnClickListener { save() }
    }

    private fun displayList(): MutableList<String> {
        return steps.mapIndexed { i, s ->
            "${i + 1}. ${s.title}"
        }.toMutableList()
    }

    private fun refresh() {
        adapter.clear()
        adapter.addAll(displayList())
        adapter.notifyDataSetChanged()

        if (steps.isEmpty()) {
            selectedIndex = -1
            listView.clearChoices()
        } else {
            selectedIndex = selectedIndex.coerceIn(0, steps.lastIndex)
            listView.setItemChecked(selectedIndex, true)
        }
    }

    private fun requireSelectedOrToast(): Boolean {
        if (selectedIndex !in steps.indices) {
            Toast.makeText(this, getString(R.string.toast_select_one), Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun addStep() {
        showEditDialog(
            title = "Add Step",
            initTitle = "",
            initDesc = ""
        ) { t, d ->
            steps.add(Step(t, d))
            selectedIndex = steps.lastIndex
            refresh()
        }
    }

    private fun editStep() {
        if (!requireSelectedOrToast()) return
        val s = steps[selectedIndex]
        showEditDialog(
            title = "Edit Step",
            initTitle = s.title,
            initDesc = s.desc
        ) { t, d ->
            steps[selectedIndex] = Step(t, d)
            refresh()
        }
    }

    private fun deleteStep() {
        if (!requireSelectedOrToast()) return
        val s = steps[selectedIndex]
        AlertDialog.Builder(this)
            .setTitle("Delete")
            .setMessage("Delete \"${s.title}\" ?")
            .setPositiveButton("Delete") { _, _ ->
                steps.removeAt(selectedIndex)
                selectedIndex = (selectedIndex - 1).coerceAtLeast(0)
                refresh()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun moveUp() {
        if (!requireSelectedOrToast()) return
        if (selectedIndex == 0) return
        val tmp = steps[selectedIndex]
        steps[selectedIndex] = steps[selectedIndex - 1]
        steps[selectedIndex - 1] = tmp
        selectedIndex -= 1
        refresh()
    }

    private fun moveDown() {
        if (!requireSelectedOrToast()) return
        if (selectedIndex == steps.lastIndex) return
        val tmp = steps[selectedIndex]
        steps[selectedIndex] = steps[selectedIndex + 1]
        steps[selectedIndex + 1] = tmp
        selectedIndex += 1
        refresh()
    }

    private fun save() {
        StepsRepo.saveSteps(this, steps)
        Toast.makeText(this, getString(R.string.toast_saved), Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun showEditDialog(
        title: String,
        initTitle: String,
        initDesc: String,
        onOk: (String, String) -> Unit
    ) {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 24, 40, 0)
        }

        val etTitle = EditText(this).apply {
            hint = "Title (big text)"
            setText(initTitle)
        }
        val etDesc = EditText(this).apply {
            hint = "Description (small text)"
            setText(initDesc)
        }

        root.addView(etTitle)
        root.addView(etDesc)

        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(root)
            .setPositiveButton("OK") { _, _ ->
                val t = etTitle.text.toString().trim().ifBlank { "STEP" }
                val d = etDesc.text.toString().trim()
                onOk(t, d)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

}
