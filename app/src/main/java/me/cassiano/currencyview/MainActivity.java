package me.cassiano.currencyview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import me.cassiano.cutecurrencyview.CuteCurrencyView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button) findViewById(R.id.button);
        final CuteCurrencyView cuteCurrencyView = (CuteCurrencyView) findViewById(R.id.currencyView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cuteCurrencyView.startAnimation();
            }
        });

    }
}
