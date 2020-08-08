package com.example.urop_application.Whitelists;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AntivirusWhitelist implements Whitelist {
    private String[] antivirusArray = new String[] {
            "com.antivirus",
            "com.rocket.tools.clean.antivirus.master",
            "com.kms.free",
            "com.avast.android.mobilesecurity",
            "com.cleanmaster.mguard",
            "com.cleanmaster.security",
            "com.symantec.mobilesecurity",
            "com.ehawk.antivirus.applock.wifi",
            "com.wsandroid.suite",
            "com.avira.android",
            "com.oneapp.max.security.pro",
            "com.bitdefender.antivirus",
            "com.atvcleaner",
            "org.malwarebytes.antimalware",
            "com.lookout"
    };

    private Set<String> antivirusSet = new HashSet<>(Arrays.asList(antivirusArray));

    @Override
    public Set<String> getSet() {
        return antivirusSet;
    }
}
