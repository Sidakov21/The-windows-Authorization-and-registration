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
import com.example.windowauthandreg.databinding.FragmentRegisterBinding
import com.example.windowauthandreg.presentation.viewmodels.AuthState
import com.example.windowauthandreg.presentation.viewmodels.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        setupObservers()
    }

    private fun setupClickListeners() {
        binding.buttonRegister.setOnClickListener {
            attemptRegistration()
        }

        binding.buttonClearForm.setOnClickListener {
            clearForm()
        }

        // Убрали кнопки переключения пароля - используем встроенные иконки Material Design

        // Убрали кнопку проверки username - можно добавить позже

        binding.buttonGoToLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        binding.buttonDemoRegister.setOnClickListener {
            fillDemoData()
        }

        // Добавляем долгое нажатие на кнопку регистрации для демо данных
        binding.buttonRegister.setOnLongClickListener {
            fillDemoData()
            true
        }
    }

    private fun attemptRegistration() {
        val username = binding.editTextUsername.text.toString()
        val email = binding.editTextEmail.text.toString()
        val password = binding.editTextPassword.text.toString()
        val confirmPassword = binding.editTextConfirmPassword.text.toString()
        val firstName = binding.editTextFirstName.text.toString()
        val lastName = binding.editTextLastName.text.toString()
        val phoneNumber = binding.editTextPhone.text.toString()

        viewModel.register(username, email, password, confirmPassword, firstName, lastName, phoneNumber)
    }

    private fun clearForm() {
        binding.editTextUsername.text?.clear()
        binding.editTextEmail.text?.clear()
        binding.editTextPassword.text?.clear()
        binding.editTextConfirmPassword.text?.clear()
        binding.editTextFirstName.text?.clear()
        binding.editTextLastName.text?.clear()
        binding.editTextPhone.text?.clear()

        binding.textInputLayoutUsername.error = null
        binding.textInputLayoutEmail.error = null
        binding.textInputLayoutPassword.error = null
        binding.textInputLayoutConfirmPassword.error = null
        binding.textInputLayoutFirstName.error = null
        binding.textInputLayoutLastName.error = null
        binding.textInputLayoutPhone.error = null
    }

    private fun fillDemoData() {
        binding.editTextUsername.setText("demo_user_${Random.nextInt(1000)}")
        binding.editTextEmail.setText("demo${Random.nextInt(1000)}@example.com")
        binding.editTextPassword.setText("Demo123!")
        binding.editTextConfirmPassword.setText("Demo123!")
        binding.editTextFirstName.setText("Демо")
        binding.editTextLastName.setText("Пользователь")
        binding.editTextPhone.setText("+79991234567")

        Toast.makeText(requireContext(), "Заполнены демо данные", Toast.LENGTH_SHORT).show()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.authState.collect { state ->
                when (state) {
                    is AuthState.Loading -> showLoading(true)
                    is AuthState.RegistrationSuccess -> {
                        showLoading(false)
                        showSuccessMessage()
                        findNavController().navigate(R.id.action_registerFragment_to_mainFragment)
                    }
                    is AuthState.Error -> {
                        showLoading(false)
                        showError(state.message)
                    }
                    else -> showLoading(false)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.validationErrors.collect { errors ->
                updateValidationErrors(errors)
            }
        }
    }

    private fun updateValidationErrors(errors: Map<String, String>) {
        binding.textInputLayoutUsername.error = errors["username"]
        binding.textInputLayoutEmail.error = errors["email"]
        binding.textInputLayoutPassword.error = errors["password"]
        binding.textInputLayoutConfirmPassword.error = errors["confirmPassword"]
        binding.textInputLayoutFirstName.error = errors["firstName"]
        binding.textInputLayoutLastName.error = errors["lastName"]
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.buttonRegister.isEnabled = !show
        binding.buttonDemoRegister.isEnabled = !show
        binding.buttonClearForm.isEnabled = !show
    }

    private fun showSuccessMessage() {
        Toast.makeText(requireContext(), "Регистрация успешна!", Toast.LENGTH_SHORT).show()
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}