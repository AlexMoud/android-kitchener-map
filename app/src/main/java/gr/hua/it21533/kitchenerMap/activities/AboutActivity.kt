package gr.hua.it21533.kitchenerMap.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity;
import gr.hua.it21533.kitchenerMap.R

import kotlinx.android.synthetic.main.activity_maps.*

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        setSupportActionBar(toolbar)
    }

}
