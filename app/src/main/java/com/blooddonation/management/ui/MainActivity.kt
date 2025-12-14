package com.blooddonation.management.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.blooddonation.management.R
import com.blooddonation.management.data.firebase.FirebaseAuthManager
import com.blooddonation.management.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    @Inject
    lateinit var firebaseAuthManager: FirebaseAuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // التحقق من تسجيل الدخول - Login is mandatory
        if (!firebaseAuthManager.isUserLoggedIn()) {
            // إذا لم يكن المستخدم مسجلاً دخول، سيتم التنقل لشاشة Login تلقائياً
            // (من خلال startDestination في nav_graph)
        }

        setSupportActionBar(binding.toolbar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_dashboard,
                R.id.nav_categories,
                R.id.nav_inventory,
                R.id.nav_distribution,
                R.id.nav_returns
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.bottomNav.setupWithNavController(navController)

        // مراقبة حالة المستخدم وعودته للـ Login إذا سجل خروج
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // إذا لم يكن المستخدم مسجلاً دخول وحاول الوصول لصفحة محمية
            if (destination.id != R.id.nav_login && !firebaseAuthManager.isUserLoggedIn()) {
                navController.navigate(R.id.nav_login)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
