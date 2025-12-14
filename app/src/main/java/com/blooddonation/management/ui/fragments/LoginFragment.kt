package com.blooddonation.management.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.blooddonation.management.R
import com.blooddonation.management.data.firebase.FirebaseAuthManager
import com.blooddonation.management.databinding.FragmentLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var firebaseAuthManager: FirebaseAuthManager

    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_In = 9001

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Check if user is already logged in
        if (firebaseAuthManager.isUserLoggedIn()) {
            findNavController().navigate(R.id.action_loginFragment_to_dashboardFragment)
            return
        }

        // Configure Google Sign In with proper error handling
        try {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build()

            googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

            binding.googleSignInButton.setOnClickListener {
                signIn()
            }
        } catch (e: Exception) {
            showError("فشل في إعداد تسجيل الدخول: ${e.message}")
        }
    }

    private fun signIn() {
        try {
            binding.progressBar.visibility = View.VISIBLE
            binding.googleSignInButton.isEnabled = false
            
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_In)
        } catch (e: Exception) {
            binding.progressBar.visibility = View.GONE
            binding.googleSignInButton.isEnabled = true
            showError("خطأ في بدء تسجيل الدخول: ${e.message}")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_In) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null && account.idToken != null) {
                    firebaseAuthWithGoogle(account.idToken!!)
                } else {
                    binding.progressBar.visibility = View.GONE
                    binding.googleSignInButton.isEnabled = true
                    showError("فشل في الحصول على رمز المصادقة")
                }
            } catch (e: ApiException) {
                binding.progressBar.visibility = View.GONE
                binding.googleSignInButton.isEnabled = true
                handleGoogleSignInError(e)
            }
        }
    }

    private fun handleGoogleSignInError(e: ApiException) {
        val errorMessage = when (e.statusCode) {
            12500 -> "خطأ في التكوين - تحقق من google-services.json وكود العميل"
            12501 -> "تم إلغاء تسجيل الدخول من قبل المستخدم"
            12502 -> "خطأ في الاتصال - تحقق من الإنترنت"
            12503 -> "خطأ في الخادم - حاول مرة أخرى لاحقاً"
            else -> "فشل تسجيل الدخول بـ Google: ${e.message}"
        }
        showError(errorMessage)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // تسجيل الدخول
                firebaseAuthManager.signInWithGoogle(idToken)
                
                // الحصول على معرّف المستخدم
                val userId = firebaseAuthManager.getUserId()
                if (userId != null) {
                    // حفظ بيانات المستخدم على Firebase
                    firebaseAuthManager.updateUserProfile(userId)
                }
                
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    showSuccess("تم تسجيل الدخول بنجاح!")
                    // الانتقال للـ Dashboard بعد حفظ البيانات
                    findNavController().navigate(R.id.action_loginFragment_to_dashboardFragment)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    binding.googleSignInButton.isEnabled = true
                    showError("خطأ في المصادقة: ${e.message}")
                }
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun showSuccess(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
