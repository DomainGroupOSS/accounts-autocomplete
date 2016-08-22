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
package au.com.domain;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;

import com.google.android.gms.auth.GoogleAuthUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import au.com.domain.library.R;

public class AccountsAutoCompleteTextView extends TextInputLayout {

    public static final int REQUEST_CODE = 1000;
    private List<String> mEmails;
    private List<String> mPriorityList;
    private boolean mAllowPrefill;
    private ArrayAdapter<String> mAdapter;
    private Activity mActivity;
    private Fragment mFragment;
    private AutoCompleteTextView mAccountsAutocomplete;

    private int mThreshold;

    public AccountsAutoCompleteTextView(Context context) {
        super(context);
        init(context);
    }

    public AccountsAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AccountsAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        addView(mAccountsAutocomplete = new AutoCompleteTextView(context));
        mAccountsAutocomplete.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        mAccountsAutocomplete.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        if (TextUtils.isEmpty(getHint())) {
            setHint("Email");
        }

        // TODO Make this a custom attr (currently does nothing)
        mThreshold = mAccountsAutocomplete.getThreshold();

        setAccountOptions();
        mAccountsAutocomplete.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b && !isPermissionGranted()) {
                    mAccountsAutocomplete.showDropDown();
                }
            }
        });
        mAccountsAutocomplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (isErrorEnabled()) {
                    setError(null);
                    setErrorEnabled(false);
                }
            }
        });
    }

    private boolean isPermissionGranted() {
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED;
    }

    private void setAccountOptions() {
        Context context = getContext();
        mEmails = getGoogleAccountEmails(context);

        if (isNonEmpty(mPriorityList)) {
            mEmails.addAll(mPriorityList);
        }

        if (isPermissionGranted()) {
            //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
            mAdapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, mEmails);
            mAccountsAutocomplete.setOnItemClickListener(null);
            if (mAccountsAutocomplete.getThreshold() < 2) {
                mAccountsAutocomplete.setThreshold(mThreshold);
            }
        } else {
            final List<String> names = Arrays.asList(context.getString(R.string.allow_accounts_suggestion));
            mAdapter = new AccountsAdapter(context, android.R.layout.simple_dropdown_item_1line, names);
            mAccountsAutocomplete.setThreshold(1);
            setAskAccountsAutoComplete(context);
        }

        mAccountsAutocomplete.setAdapter(mAdapter);

        if (mAllowPrefill && !mEmails.isEmpty() && isNonEmpty(mPriorityList)) {
            setChosenAccountName(mPriorityList.get(0));
        }
    }

    private boolean isNonEmpty(Collection collection) {
        return collection != null && !collection.isEmpty();
    }

    /**
     * If @param allowPrefill is TRUE, the first entry in emailOptions will be used to prefill the
     * EditText.
     *
     * @param emailOptions
     */
    public void setEmailOptions(@Nullable List<String> emailOptions, boolean allowPrefill) {
        mPriorityList = emailOptions;
        mAllowPrefill = allowPrefill;

        setAccountOptions();
    }

    public void setParentActivity(Activity activity) {
        mActivity = activity;
    }

    public void setParentFragment(Fragment fragment) {
        mFragment = fragment;
    }

    public void addDropdownOptions(List<String> options) {
        mEmails.addAll(options);
        mAdapter.notifyDataSetChanged();
    }

    private void setAskAccountsAutoComplete(final Context context) {
        mAccountsAutocomplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mAccountsAutocomplete.setText("");
                hideSoftKeyboard(context, AccountsAutoCompleteTextView.this.getWindowToken());

                if (mActivity == null && mFragment == null) {
                    throw new IllegalStateException("No calling Activity or Fragment declared. Call either setParentActivity() or setParentFragment().");
                }

                if (mActivity != null) {
                    ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.GET_ACCOUNTS}, REQUEST_CODE);
                } else {
                    mFragment.requestPermissions(new String[]{Manifest.permission.GET_ACCOUNTS}, REQUEST_CODE);
                }
            }
        });
    }

    /**
     * Clients would need to call this method because Android only calls back the host activity.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onPermissionResponse(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean granted = REQUEST_CODE == requestCode && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;

        if (granted) {
            setAccountOptions();
            mAccountsAutocomplete.showDropDown();
        }
    }


    private void setChosenAccountName(@NonNull String accountName) {
        mAccountsAutocomplete.setText(accountName);
        if (isErrorEnabled()) {
            setError(null);
        }
    }

    private List<String> getGoogleAccountEmails(Context context) {
        List<String> emails = new ArrayList<>();
        Account[] accounts = AccountManager.get(context).getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
        for (Account account : accounts) {
            if (android.util.Patterns.EMAIL_ADDRESS.matcher(account.name).matches()) {
                emails.add(account.name);
            }
        }
        Collections.sort(emails, String.CASE_INSENSITIVE_ORDER);
        return emails;
    }

    private void hideSoftKeyboard(Context context, IBinder windowToken) {
        if (context == null) {
            return;
        }

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(windowToken, 0);
    }
    public String getText() {
        return mAccountsAutocomplete.getText().toString();
    }

    public AutoCompleteTextView getEditText() {
        return mAccountsAutocomplete;
    }

    public boolean isEmailValid() {
        final String input = mAccountsAutocomplete.getText().toString();
        return !TextUtils.isEmpty(input) && Patterns.EMAIL_ADDRESS.matcher(input).matches();
    }

    @Override
    public void setError(@Nullable CharSequence error) {
        setErrorEnabled(true);
        super.setError(error);
    }

    public void setText(String text) {
        mAccountsAutocomplete.setText(text);
    }

    class AccountsAdapter extends ArrayAdapter<String> {

        private Filter mFilter;

        public AccountsAdapter(Context context, int resource, final List<String> objects) {
            super(context, resource, objects);
            mFilter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = objects;
                    filterResults.count = objects.size();
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    notifyDataSetChanged();
                }
            };
        }

        @NonNull
        @Override
        public Filter getFilter() {
            return mFilter;
        }
    }
}
