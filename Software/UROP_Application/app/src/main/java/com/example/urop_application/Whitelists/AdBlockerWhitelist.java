package com.example.urop_application.Whitelists;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AdBlockerWhitelist implements Whitelist {
    private String[] adBlockerArray = new String[]{
            "com.hsv.freeadblockerbrowser",
            "com.betafish.adblocksbrowser",
            "org.adblockplus.browser",
            "com.rocketshipapps.adblockfast",
            "kr.co.lylstudio.unicorn",
            "com.brave.browser",
            "org.adblockplus.adblockplussbrowser",
            "com.ksmobile.cb",
            "co.crystalapp.crystal",
            "tr.abak.simsekTarayici",
            "mobi.mgeek.TunnyBrowser",
            "com.cosmic.privacybrowser",
            "com.hsv.privatebrowser",
            "adblock.browser.lightning",
            "com.adguard.android.contentblocker"
    };

    private Set<String> adBlockerSet = new HashSet<>(Arrays.asList(adBlockerArray));

    @Override
    public Set<String> getSet() {
        return adBlockerSet;
    }
}
