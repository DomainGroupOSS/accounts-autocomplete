package au.com.domain.accountsautocomplete;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import au.com.domain.AccountsAutoCompleteTextView;

public class MainActivity extends AppCompatActivity {

    private AccountsAutoCompleteTextView mAccountsAutoCompleteTextView;
    private AccountsAutoCompleteTextView mAutoCompleteWithPrefill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAccountsAutoCompleteTextView = (AccountsAutoCompleteTextView) findViewById(R.id.accounts_autocomplete);
        mAccountsAutoCompleteTextView.setParentActivity(this);

        mAutoCompleteWithPrefill = (AccountsAutoCompleteTextView) findViewById(R.id.accounts_autocomplete_prefill);
        mAutoCompleteWithPrefill.setParentActivity(this);

        List<String> emailOptions = new ArrayList<>();
        emailOptions.add("Email 1");
        emailOptions.add("Email 2");
        emailOptions.add("Email 3");
        mAutoCompleteWithPrefill.setEmailOptions(emailOptions, true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mAccountsAutoCompleteTextView.onPermissionResponse(requestCode, permissions, grantResults);
    }
}
