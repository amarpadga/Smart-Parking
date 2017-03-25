package com.example.akhil.smartparking;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class BaseActivity extends AppCompatActivity{

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu1:
                Toast.makeText(this, "Clicked: Parking View", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                this.startActivity(intent);
                break;

            case R.id.menu2:
                Toast.makeText(this, "Clicked to View Barcode", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(this, Barcode.class);
                this.startActivity(intent2);
                break;

            case R.id.menu3:
                Toast.makeText(this, "Clicked: Login/Register", Toast.LENGTH_SHORT).show();
                break;

            case R.id.menu4:
                Toast.makeText(this, "Clicked: My Reservations", Toast.LENGTH_SHORT).show();
                Intent intent3 = new Intent(this, MyReservations.class);
                this.startActivity(intent3);
                break;

            case R.id.submenu1:
                Toast.makeText(this, "Clicked: Login", Toast.LENGTH_SHORT).show();
                Intent intent4 = new Intent(this, LoginActivity.class);
                this.startActivity(intent4);
                break;

            case R.id.submenu2:
                Toast.makeText(this, "Clicked: Sign Up", Toast.LENGTH_SHORT).show();
                Intent intent5 = new Intent(this, Register.class);
                this.startActivity(intent5);
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
    return true;
    }
}