package com.igor.passwordgenerator

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var tvPassword: TextView
    private lateinit var tvLength: TextView
    private lateinit var seekBarLength: SeekBar
    private lateinit var switchUppercase: SwitchCompat
    private lateinit var switchLowercase: SwitchCompat
    private lateinit var switchNumbers: SwitchCompat
    private lateinit var switchSymbols: SwitchCompat
    private lateinit var switchExcludeSimilar: SwitchCompat
    private lateinit var btnGenerate: Button
    private lateinit var ivCopy: ImageView
    private var passwordLength = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        tvPassword = findViewById(R.id.tvPassword)
        tvLength = findViewById(R.id.tvLength)
        seekBarLength = findViewById(R.id.seekBarLength)
        switchUppercase = findViewById(R.id.switchUppercase)
        switchLowercase = findViewById(R.id.switchLowercase)
        switchNumbers = findViewById(R.id.switchNumbers)
        switchSymbols = findViewById(R.id.switchSymbols)
        switchExcludeSimilar = findViewById(R.id.switchExcludeSimilar)
        btnGenerate = findViewById(R.id.btnGenerate)
        ivCopy = findViewById(R.id.ivCopy)

        seekBarLength.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                passwordLength = progress
                tvLength.text = "Tamanho da senha: $passwordLength"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        btnGenerate.setOnClickListener {
            if (!switchUppercase.isChecked && !switchLowercase.isChecked && !switchNumbers.isChecked && !switchSymbols.isChecked) {
                Toast.makeText(this, "Escolha pelo menos uma opção!", Toast.LENGTH_SHORT).show()
            } else {
                try {
                    val password = generatePassword()
                    tvPassword.text = password

                    if (!ivCopy.hasOnClickListeners()) {
                        ivCopy.alpha = 1f
                        ivCopy.setOnClickListener {
                            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Senha", password)

                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(this, "Senha copiada!", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: IllegalArgumentException) {
                    Toast.makeText(
                        this,
                        e.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun generatePassword(): String {
        var chars = ""

        if (switchUppercase.isChecked) chars += "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        if (switchLowercase.isChecked) chars += "abcdefghijklmnopqrstuvwxyz"
        if (switchNumbers.isChecked) chars += "0123456789"
        if (switchSymbols.isChecked) chars += "!@#$%&*()-_=+[]{};:,.?/~^´`"

        if (chars.length < passwordLength && switchExcludeSimilar.isChecked) {
            throw IllegalArgumentException("Impossível gerar senha usando as configurações atuais.")
        }

        var password = ""

        while (password.length < passwordLength) {
            val char = chars[Random.nextInt(chars.length)]
            password += if (switchExcludeSimilar.isChecked && password.contains(char)) "" else char
        }

        return password
    }
}