package eu.kudan.ar.input;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

/**
 * Created by LightSnail on 2018/7/13.
 */

public class KeyUtils {

    /**
     * Gets the KudanCV API key from the Android Manifest.
     *
     * The API key should be contained in a tag of format:
     * <meta-data>
     *     android:name="${PACKAGE_NAME}.API_KEY
     *     android:value="${YOUR_API_KEY}
     * </meta-data>
     *
     * @return the API key
     */
    public static String getAPIKey(Activity activity) {

        String appPackageName = activity.getPackageName();

        try {
            ApplicationInfo app = activity
                    .getPackageManager()
                    .getApplicationInfo(appPackageName, PackageManager.GET_META_DATA);

            Bundle bundle = app.metaData;

          //  String apiKeyID = appPackageName + ".API_KEY";
            String apiKeyID = "eu.kudan.ar" + ".API_KEY";

            if (bundle == null) {
                throw new RuntimeException("No manifest meta-data tags exist.\n\nMake sure the AndroidManifest.xml file contains a <meta-data\n\tandroid:name=\"" + apiKeyID + "\"\n\tandroid:value=\"${YOUR_API_KEY}\"></meta-data>\n");
            }

            String apiKey = bundle.getString(apiKeyID);

            if (apiKey == null) {
                throw new RuntimeException("Could not get API Key from Android Manifest meta-data.\n\nMake sure the AndroidManifest.xml file contains a <meta-data\n\tandroid:name=\"" + apiKeyID + "\"\n\tandroid:value=\"${YOUR_API_KEY}\"></meta-data>\n");
            }

            if (apiKey.isEmpty()) {
                throw new RuntimeException("Your API Key from Android Manifest meta-data appears to be empty.\n\nMake sure the AndroidManifest.xml file contains a <meta-data\n\tandroid:name=\"" + apiKeyID + "\"\n\tandroid:value=\"${YOUR_API_KEY}\"></meta-data>\n");
            }

            return apiKey;

        }
        catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Cannot find Package with name \"" + appPackageName + "\". Cannot load API key.");
        }
    }

}
