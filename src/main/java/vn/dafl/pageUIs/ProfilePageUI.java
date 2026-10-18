package vn.dafl.pageUIs;

public class ProfilePageUI {
    public static final String PROFILE_PAGE = "css=.profile-page";
    public static final String PROFILE_FULLNAME_INPUT = "xpath=(//section[contains(@class,'profile-page')]//form//input)[1]";
    public static final String PROFILE_PHONE_INPUT = "xpath=(//section[contains(@class,'profile-page')]//form//input)[2]";
    public static final String PROFILE_EMAIL_INPUT = "xpath=(//section[contains(@class,'profile-page')]//form//input)[3]";
    public static final String PROFILE_ADDRESS_INPUT = "xpath=(//section[contains(@class,'profile-page')]//form//input)[4]";
    public static final String PROFILE_NOTES_INPUT = "xpath=//section[contains(@class,'profile-page')]//form//textarea";
    public static final String SAVE_PROFILE_BTN = "xpath=//section[contains(@class,'profile-page')]//button[@type='submit']";
    public static final String BACK_TO_HOME_BTN = "xpath=//section[contains(@class,'profile-page')]//div[contains(@class,'page-header')]//button[1]";
}
