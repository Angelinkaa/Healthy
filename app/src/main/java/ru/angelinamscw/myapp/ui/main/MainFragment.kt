package ru.angelinamscw.myapp.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import ru.angelinamscw.domain.Record
import ru.angelinamscw.myapp.R
import ru.angelinamscw.myapp.databinding.MineFragmentBinding
import ru.angelinamscw.myapp.ui.common.ILongClicked

class MainFragment : Fragment() {
    private lateinit var adapter: MainAdapter
    private lateinit var viewModel: MainViewModel
    private var _binding: MineFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        adapter = MainAdapter()
        adapter.itemLongClicked = object : ILongClicked {
            override fun onItemLongClicked(view: View, position: Int, itemObject: Any) {
                if (itemObject is Record) {
                    val pop = PopupMenu(view.context, view)
                    pop.inflate(R.menu.record_context_menu)
                    pop.setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.action_delete_record -> {
                                viewModel.deleteRecord(itemObject)
                            }
                        }
                        true
                    }
                    pop.show()
                }
            }
        }
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        _binding = MineFragmentBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mineList = binding.rvMain
        mineList.adapter = adapter
        mineList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        viewModel.fetchCurrentList()
        viewModel.listForMain.observe(viewLifecycleOwner) {
            adapter.items = it
            adapter.notifyDataSetChanged()
        }

        binding.fab.setOnClickListener {
            showDialogInputTitleNewNote()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showDialogInputTitleNewNote() {
        val view: View = layoutInflater.inflate(R.layout.input_new_record_dialog, null)
        val systolicPressure = view.findViewById<View>(R.id.et_systolic_pressure) as EditText
        val diastolicPressure = view.findViewById<View>(R.id.et_diastolic_pressure) as EditText
        val heartRate = view.findViewById<View>(R.id.et_heartRate) as EditText
        val inputTitle: AlertDialog = AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_input_title)
            .setView(view)
            .setIcon(R.drawable.ic_baseline_input_24)
            .setPositiveButton(R.string.btn_ok_text) { _, _ ->
                viewModel.addNewRecord(
                    systolicPressure.text.toString(),
                    diastolicPressure.text.toString(),
                    heartRate.text.toString()
                )
            }
            .setNeutralButton(R.string.btn_cancel_text) { _, _ ->
                Toast.makeText(requireContext(), "???????????? ???? ??????????????????", Toast.LENGTH_SHORT).show();
            }
            .setCancelable(false)
            .create()
        inputTitle.show()
    }
}