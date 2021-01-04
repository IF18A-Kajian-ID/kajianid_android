package com.kajianid.ustadz.ui.intro

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.github.appintro.AppIntro2
import com.github.appintro.AppIntroFragment
import com.github.appintro.AppIntroPageTransformerType
import com.kajianid.ustadz.R
import com.kajianid.ustadz.ui.login.LoginActivity

class IntroActivity : AppIntro2() {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Make sure you don't call setContentView!
        // Call addSlide passing your Fragments.
        // You can use AppIntroFragment to use a pre-built fragment
        addSlides()
        // Ask for required permission
        askForPermissions(
                permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                slideNumber = 1, // first slide
                required = true)

        /* APPEARANCES */
        // Hide/Show the status Bar
        showStatusBar(true)

        // Wizard Mode
        isWizardMode = true

        // You can customize your parallax parameters in the constructors.
        setTransformer(AppIntroPageTransformerType.Parallax(
                titleParallaxFactor = 1.0,
                imageParallaxFactor = -1.0,
                descriptionParallaxFactor = 2.0
        ))

        // Toggle Indicator Visibility
        isIndicatorEnabled = true
        // Change Indicator Color
        setIndicatorColor(
                selectedIndicatorColor = getColor(R.color.biru),
                unselectedIndicatorColor = getColor(R.color.abuabu)
        )
    }

    private fun addSlides() {
        addSlide(AppIntroFragment.newInstance(
                title = "Jalin hubungan bersama masyarakat",
                description = """
                    Buatlah pengumuman dan informasi terkait kajian yang akan Anda adakan! 
                """.trimIndent(),
                titleColor = Color.BLACK,
                imageDrawable = R.drawable.intro1,
                descriptionColor = Color.BLACK,
                backgroundColor = Color.WHITE
        ))
        addSlide(AppIntroFragment.newInstance(
                title = "Bantu mereka meningkatkan pengetahuannya",
                description = """
                    Anda juga dapat membuat posting Islami yang akan disebarkan ke seluruh kalangan masyarakat melalui Kajian.ID!
                """.trimIndent(),
                titleColor = Color.BLACK,
                imageDrawable = R.drawable.intro3,
                descriptionColor = Color.BLACK,
                backgroundColor = Color.WHITE
        ))
    }

    override fun onUserDeniedPermission(permissionName: String) {
        // User pressed "Deny" on the permission dialog
        Toast.makeText(
                applicationContext,
                "Maaf, kami perlu izin untuk membaca berkas agar kami bisa mengunggah foto atau poster kajian ke server kami!",
                Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        // Decide what to do when the user clicks on "Done"
        val sp: SharedPreferences = getSharedPreferences("root_preferences", Context.MODE_PRIVATE)
        with(sp.edit()) {
            putBoolean("first", true)
            commit()
        }
        val home = Intent(this@IntroActivity, LoginActivity::class.java)
        startActivity(home)
        finish()
    }
}