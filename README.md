# accounts-autocomplete
An M-compatible custom AutoCompleteTextView that shows the user's Google accounts as autocomplete suggestions.  

This widget is based on the GMail behaviour of subtly asking for permissions for autcompleting email addresses.  

Before permission is granted:  
![Allow Suggestions](website/allow_suggestions.png)

After permission is granted:  
![With Suggestions](website/suggestions_dropdown.png)

## Usage
```
compile 'au.com.domain:accounts-autocomplete:1.0.0'
```

Drop in `au.com.domain.AccountsAutoCompleteTextView` in your layout:
```
<au.com.domain.AccountsAutoCompleteTextView
        android:id="@+id/accounts_autocomplete"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />
```

and set it up in the `Activity`:
```java
mAccountsAutoCompleteTextView.setParentActivity(this);
```
or `Fragment`:
```java
mAccountsAutoCompleteTextView.setParentFragment(this);
```

You would then need to override the `onRequestPermissionsResult` callback:
```java
@Override
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    mAccountsAutoCompleteTextView.onPermissionResponse(requestCode, permissions, grantResults);
}
```

