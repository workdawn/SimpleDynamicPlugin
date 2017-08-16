package android.content.pm;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.jar.Attributes;

/**
 * 用来伪装系统的hide类ManifestDigest,来源于滴滴开源项目VirtualAPK
 * 地址:https://github.com/didi/VirtualAPK/tree/master/AndroidStub
 */
public class ManifestDigest implements Parcelable{
    ManifestDigest(final byte[] digest) {
        throw new RuntimeException("Stub!");
    }

    private ManifestDigest(final Parcel source) {
        throw new RuntimeException("Stub!");
    }

    static ManifestDigest fromAttributes(final Attributes attributes) {
        throw new RuntimeException("Stub!");
    }

    @Override
    public int describeContents() {
        throw new RuntimeException("Stub!");
    }

    @Override
    public boolean equals(Object o) {
        throw new RuntimeException("Stub!");
    }

    @Override
    public int hashCode() {
        throw new RuntimeException("Stub!");
    }

    @Override
    public String toString() {
        throw new RuntimeException("Stub!");
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        throw new RuntimeException("Stub!");
    }

    public static final Parcelable.Creator<ManifestDigest> CREATOR = new Parcelable.Creator<ManifestDigest>() {
        public ManifestDigest createFromParcel(Parcel source) {
            return new ManifestDigest(source);
        }

        public ManifestDigest[] newArray(int size) {
            return new ManifestDigest[size];
        }
    };
}
