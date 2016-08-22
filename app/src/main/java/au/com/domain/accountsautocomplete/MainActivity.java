/**
 * Copyright 2016 Domain.com.au
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
        emailOptions.add("my_email@gmail.com");
        emailOptions.add("feedback@domain.com.au");
        mAutoCompleteWithPrefill.setEmailOptions(emailOptions, true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mAccountsAutoCompleteTextView.onPermissionResponse(requestCode, permissions, grantResults);
        mAutoCompleteWithPrefill.onPermissionResponse(requestCode, permissions, grantResults);
    }
}
