package com.lashgo.android.service.handlers;

/**
 * User: eugene.petriyov
 * Date: 25.06.13
 * Time: 14:16
 */
public class RestHandlerFactory {


    public static final String ACTION_LOGIN = "login";

    public static final String ACTION_REGISTER = "register";

    public static final String ACTION_SOCIAL_SIGN_IN = "sign_in_social";

    public static final String ACTION_GCM_REGISTER_ID = "gcm_register_id";

    public static final String ACTION_GET_LAST_CHECK = "get_last_check";

    public static BaseIntentHandler getIntentHandler(String action) {
        if (action.equals(ACTION_LOGIN)) {
            return new LoginHandler();
        } else if (action.equals(ACTION_REGISTER)) {
            return new RegisterHandler();
        } else if (action.equals(ACTION_SOCIAL_SIGN_IN)) {
            return new SocialSignInHandler();
        } else if (action.equals(ACTION_GCM_REGISTER_ID)) {
            return new GcmRegisterHandler();
        } else if (action.equals(ACTION_GET_LAST_CHECK)) {
            return new GetLastCheckHandler();
        }
        else {
            throw new IllegalArgumentException("illegal action - " + action);
        }
    }
}
