package com.example.animals.view


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.animals.R
import com.example.animals.model.Animal
import com.example.animals.viewmodel.ListViewModel
import kotlinx.android.synthetic.main.fragment_list.*


/**
 * A simple [Fragment] subclass.
 *
 */
class ListFragment : Fragment() {

    private lateinit var viewModel: ListViewModel
    private val listAdapter = AnimalListAdapter(arrayListOf())

    private val animalListDataObserver = Observer<List<Animal>>{list ->

        list?.let {

            animailList.visibility = View.VISIBLE
            listAdapter.updateAnimalList(it)
        }

    }

    private val loadingLiveDataObserver = Observer<Boolean>{isLoading ->

        loadingview.visibility = ( if (isLoading) View.VISIBLE else View.GONE)


        if (isLoading){
            animailList.visibility = View.GONE
            listError.visibility = View.GONE


        }

    }

    private val errorLiveDataObserver = Observer<Boolean> {isError ->

        listError.visibility = ( if (isError) View.VISIBLE else View.GONE)
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(ListViewModel::class.java)
        viewModel.animals.observe(this, animalListDataObserver)
        viewModel.loading.observe(this, loadingLiveDataObserver)
        viewModel.loadError.observe(this, errorLiveDataObserver)
        viewModel.refresh()

        animailList.apply {
           layoutManager = GridLayoutManager(context, 2)
            adapter = listAdapter
        }

        refreshlayout.setOnRefreshListener {
            animailList.visibility = View.GONE
            listError.visibility = View.GONE
            loadingview.visibility = View.GONE
            viewModel.refresh()
            refreshlayout.isRefreshing = false

        }



    }



}
