package it.niedermann.owncloud.notes.shared.model;

import android.graphics.Color;
import android.util.Log;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.HttpException;
import com.nextcloud.android.sso.exceptions.NextcloudHttpRequestFailedException;

import org.json.JSONException;
import org.json.JSONObject;

import it.niedermann.android.util.ColorUtil;

import static java.net.HttpURLConnection.HTTP_UNAVAILABLE;

/**
 * This entity class is used to return relevant data of the HTTP response.
 */
public class Capabilities {

    private static final String TAG = Capabilities.class.getSimpleName();

    private static final String JSON_OCS = "ocs";
    private static final String JSON_OCS_META = "meta";
    private static final String JSON_OCS_META_STATUSCODE = "statuscode";
    private static final String JSON_OCS_DATA = "data";
    private static final String JSON_OCS_DATA_CAPABILITIES = "capabilities";
    private static final String JSON_OCS_DATA_CAPABILITIES_NOTES = "notes";
    private static final String JSON_OCS_DATA_CAPABILITIES_NOTES_API_VERSION = "api_version";
    private static final String JSON_OCS_DATA_CAPABILITIES_THEMING = "theming";
    private static final String JSON_OCS_DATA_CAPABILITIES_THEMING_COLOR = "color";
    private static final String JSON_OCS_DATA_CAPABILITIES_THEMING_COLOR_TEXT = "color-text";

    private String apiVersion = null;

    @ColorInt
    private Integer color = -16743735;
    @ColorInt
    private Integer textColor = -16777216;
    @Nullable
    private final String eTag;

    public Capabilities(@NonNull String response, @Nullable String eTag) throws NextcloudHttpRequestFailedException {
        this.eTag = eTag;
        final JSONObject ocs;
        try {
            ocs = new JSONObject(response).getJSONObject(JSON_OCS);
            if (ocs.has(JSON_OCS_META)) {
                final JSONObject meta = ocs.getJSONObject(JSON_OCS_META);
                if (meta.has(JSON_OCS_META_STATUSCODE)) {
                    if (meta.getInt(JSON_OCS_META_STATUSCODE) == HTTP_UNAVAILABLE) {
                        Log.i(TAG, "Capabilities Endpoint: This instance is currently in maintenance mode.");
                        throw new NextcloudHttpRequestFailedException(HTTP_UNAVAILABLE, new HttpException(HTTP_UNAVAILABLE));
                    }
                }
            }
            if (ocs.has(JSON_OCS_DATA)) {
                final JSONObject data = ocs.getJSONObject(JSON_OCS_DATA);
                if (data.has(JSON_OCS_DATA_CAPABILITIES)) {
                    final JSONObject capabilities = data.getJSONObject(JSON_OCS_DATA_CAPABILITIES);
                    if (capabilities.has(JSON_OCS_DATA_CAPABILITIES_NOTES)) {
                        final JSONObject notes = capabilities.getJSONObject(JSON_OCS_DATA_CAPABILITIES_NOTES);
                        if (notes.has(JSON_OCS_DATA_CAPABILITIES_NOTES_API_VERSION)) {
                            this.apiVersion = notes.getString(JSON_OCS_DATA_CAPABILITIES_NOTES_API_VERSION);
                        }
                    }
                    if (capabilities.has(JSON_OCS_DATA_CAPABILITIES_THEMING)) {
                        final JSONObject theming = capabilities.getJSONObject(JSON_OCS_DATA_CAPABILITIES_THEMING);
                        if (theming.has(JSON_OCS_DATA_CAPABILITIES_THEMING_COLOR)) {
                            try {
                                this.color = Color.parseColor(ColorUtil.INSTANCE.formatColorToParsableHexString(theming.getString(JSON_OCS_DATA_CAPABILITIES_THEMING_COLOR)));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (theming.has(JSON_OCS_DATA_CAPABILITIES_THEMING_COLOR_TEXT)) {
                            try {
                                this.textColor = Color.parseColor(ColorUtil.INSTANCE.formatColorToParsableHexString(theming.getString(JSON_OCS_DATA_CAPABILITIES_THEMING_COLOR_TEXT)));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getApiVersion() {
        return apiVersion;
    }

    @Nullable
    public String getETag() {
        return eTag;
    }

    public Integer getColor() {
        return color;
    }

    public Integer getTextColor() {
        return textColor;
    }

    @NonNull
    @Override
    public String toString() {
        return "Capabilities{" +
                "apiVersion='" + apiVersion + '\'' +
                ", color=" + color +
                ", textColor=" + textColor +
                ", eTag='" + eTag + '\'' +
                '}';
    }
}