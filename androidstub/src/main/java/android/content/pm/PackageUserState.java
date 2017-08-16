package android.content.pm;

import android.util.ArraySet;

/**
 * 用来伪装系统的hide类PackageUserState,来源于滴滴开源项目VirtualAPK
 * 地址:https://github.com/didi/VirtualAPK/tree/master/AndroidStub
 */
public class PackageUserState {
    public boolean stopped;
    public boolean notLaunched;
    public boolean installed;
    public boolean hidden; // Is the app restricted by owner / admin
    public int enabled;
    public boolean blockUninstall;

    public String lastDisableAppCaller;

    public ArraySet<String> disabledComponents;
    public ArraySet<String> enabledComponents;

    public int domainVerificationStatus;
    public int appLinkGeneration;

    public PackageUserState() {
        throw new RuntimeException("Stub!");
    }

    public PackageUserState(final PackageUserState o) {
        throw new RuntimeException("Stub!");
    }
}
