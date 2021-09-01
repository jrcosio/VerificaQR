package com.jrblanco.verificaqr;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.jrblanco.verificaqr.databinding.ActivityInfoBinding;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityInfoBinding binding = ActivityInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bntOK.setOnClickListener(v -> this.finish());
    }
}