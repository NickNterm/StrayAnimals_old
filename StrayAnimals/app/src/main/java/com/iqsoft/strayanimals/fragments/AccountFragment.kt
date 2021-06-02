package com.iqsoft.strayanimals.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.iqsoft.strayanimals.Constants
import com.iqsoft.strayanimals.R
import com.iqsoft.strayanimals.models.Account
import kotlinx.android.synthetic.main.account_fragment.view.*


class AccountFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var account: Account? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            account = it.getParcelable(Constants.IntentAccount)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val lf = requireActivity().layoutInflater
        val v: View = lf.inflate(R.layout.account_fragment, container, false)
        if(account != null){
            v.AccountFragmentName.text = account?.name
            v.AccountFragmentEmail.text = account?.email
            v.AccountFragmentPhone.text = account?.phone
        }
        return v
    }


    companion object {
        @JvmStatic
        fun newInstance(account: Account) =
            AccountFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(Constants.IntentAccount, account)
                }
            }
    }
}