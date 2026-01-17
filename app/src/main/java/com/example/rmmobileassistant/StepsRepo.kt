package com.example.rmmobileassistant

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object StepsRepo {
    private const val PREF = "rm_mobile_assistant"
    private const val KEY = "field_test_steps_v1"

    fun loadSteps(context: Context): MutableList<Step> {
        val sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val raw = sp.getString(KEY, null)

        fun makeDefaults(): MutableList<Step> {
            val defaults = defaultSteps(context)
            saveSteps(context, defaults)
            return defaults.toMutableList()
        }

        if (raw.isNullOrBlank()) {
            return makeDefaults()
        }

        return try {
            val arr = JSONArray(raw)
            val out = mutableListOf<Step>()
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                out.add(
                    Step(
                        title = o.optString("title", context.getString(R.string.dialog_default_step)),
                        desc = o.optString("desc", "")
                    )
                )
            }
            if (out.isEmpty()) makeDefaults() else out
        } catch (_: Exception) {
            makeDefaults()
        }
    }


    fun saveSteps(context: Context, steps: List<Step>) {
        val arr = JSONArray()
        for (s in steps) {
            val o = JSONObject()
            o.put("title", s.title)
            o.put("desc", s.desc)
            arr.put(o)
        }
        val sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        sp.edit().putString(KEY, arr.toString()).apply()
    }

    private fun defaultSteps(context: Context): List<Step> = listOf(
        Step(context.getString(R.string.step_check_power_title), context.getString(R.string.step_check_power_desc)),
        Step(context.getString(R.string.step_check_link_title), context.getString(R.string.step_check_link_desc)),
        Step(context.getString(R.string.step_move_forward_title), context.getString(R.string.step_move_forward_desc)),
        Step(context.getString(R.string.step_move_backward_title), context.getString(R.string.step_move_backward_desc)),
        Step(context.getString(R.string.step_strafe_left_title), context.getString(R.string.step_strafe_left_desc)),
        Step(context.getString(R.string.step_strafe_right_title), context.getString(R.string.step_strafe_right_desc)),
        Step(context.getString(R.string.step_rotate_left_title), context.getString(R.string.step_rotate_left_desc)),
        Step(context.getString(R.string.step_rotate_right_title), context.getString(R.string.step_rotate_right_desc)),
        Step(context.getString(R.string.step_gimbal_test_title), context.getString(R.string.step_gimbal_test_desc)),
        Step(context.getString(R.string.step_done_title), context.getString(R.string.step_done_desc)),
    )
}
