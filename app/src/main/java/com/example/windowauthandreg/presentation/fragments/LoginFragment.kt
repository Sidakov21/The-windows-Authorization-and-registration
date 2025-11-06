package com.example.windowauthandreg.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.windowauthandreg.R
import com.example.windowauthandreg.databinding.FragmentLoginBinding
import com.example.windowauthandreg.presentation.viewmodels.AuthState
import com.example.windowauthandreg.presentation.viewmodels.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    // Используем обычный ViewModel без Hilt
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        setupObservers()
    }

    private fun setupClickListeners() {
        binding.buttonLogin.setOnClickListener {
            attemptLogin()
        }

        binding.buttonDemoLogin.setOnClickListener {
            fillDemoCredentials()
        }

        binding.buttonGoToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.buttonGuestLogin.setOnClickListener {
            loginAsGuest()
        }
    }

    private fun attemptLogin() {
        val identifier = binding.editTextIdentifier.text.toString()
        val password = binding.editTextPassword.text.toString()
        val rememberMe = binding.checkboxRememberMe.isChecked

        viewModel.login(identifier, password, rememberMe)
    }

    private fun fillDemoCredentials() {
        binding.editTextIdentifier.setText("demo@example.com")
        binding.editTextPassword.setText("12345678")
        binding.checkboxRememberMe.isChecked = true

        Toast.makeText(requireContext(), "Демо данные заполнены", Toast.LENGTH_SHORT).show()
    }

    private fun loginAsGuest() {
        findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.authState.collect { state ->
                when (state) {
                    is AuthState.Loading -> showLoading(true)
                    is AuthState.LoginSuccess -> {
                        showLoading(false)
                        showSuccessMessage("Вход успешен!")
                        findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
                    }
                    is AuthState.Error -> {
                        showLoading(false)
                        showError(state.message)
                    }
                    else -> showLoading(false)
                }
            }
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.buttonLogin.isEnabled = !show
    }

    private fun showSuccessMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}