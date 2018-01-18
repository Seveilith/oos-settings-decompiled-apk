package com.android.settings;

import com.android.settings.applications.AppOpsSummary;
import com.android.settings.fingerprint.FingerprintEnrollIntroduction;
import com.android.settings.fingerprint.FingerprintSettings;

public class Settings
  extends SettingsActivity
{
  public static class AccessibilityContrastSettingsActivity
    extends SettingsActivity
  {}
  
  public static class AccessibilityDaltonizerSettingsActivity
    extends SettingsActivity
  {}
  
  public static class AccessibilityInversionSettingsActivity
    extends SettingsActivity
  {}
  
  public static class AccessibilitySettingsActivity
    extends RencentSettings
  {}
  
  public static class AccountSettingsActivity
    extends SettingsActivity
  {}
  
  public static class AccountSyncSettingsActivity
    extends SettingsActivity
  {}
  
  public static class AccountSyncSettingsInAddAccountActivity
    extends SettingsActivity
  {}
  
  public static class AdvancedAppsActivity
    extends SettingsActivity
  {}
  
  public static class AdvancedWifiSettingsActivity
    extends SettingsActivity
  {}
  
  public static class AllApplicationsActivity
    extends SettingsActivity
  {}
  
  public static class AndroidBeamSettingsActivity
    extends SettingsActivity
  {}
  
  public static class ApnEditorActivity
    extends SettingsActivity
  {}
  
  public static class ApnSettingsActivity
    extends SettingsActivity
  {}
  
  public static class AppDrawOverlaySettingsActivity
    extends SettingsActivity
  {}
  
  public static class AppMemoryUsageActivity
    extends SettingsActivity
  {}
  
  public static class AppNotificationSettingsActivity
    extends SettingsActivity
  {}
  
  public static class AppOpsSummaryActivity
    extends SettingsActivity
  {
    public boolean isValidFragment(String paramString)
    {
      if (AppOpsSummary.class.getName().equals(paramString)) {
        return true;
      }
      return super.isValidFragment(paramString);
    }
  }
  
  public static class AppWriteSettingsActivity
    extends SettingsActivity
  {}
  
  public static class ApplicationSettingsActivity
    extends SettingsActivity
  {}
  
  public static class AvailableVirtualKeyboardActivity
    extends SettingsActivity
  {}
  
  public static class BackgroundCheckSummaryActivity
    extends SettingsActivity
  {}
  
  public static class BatterySaverSettingsActivity
    extends RencentSettings
  {}
  
  public static class BgOptimizeAppListActivity
    extends SettingsActivity
  {}
  
  public static class BgOptimizeSwitchActivity
    extends SettingsActivity
  {}
  
  public static class BluetoothSettingsActivity
    extends RencentSettings
  {}
  
  public static class CaptioningSettingsActivity
    extends SettingsActivity
  {}
  
  public static class ChooseAccountActivity
    extends SettingsActivity
  {}
  
  public static class ConditionProviderSettingsActivity
    extends SettingsActivity
  {}
  
  public static class ConfigureNotificationSettingsActivity
    extends RencentSettings
  {}
  
  public static class CryptKeeperSettingsActivity
    extends SettingsActivity
  {}
  
  public static class CustomSettings
    extends SettingsActivity
  {}
  
  public static class DataUsageSummaryActivity
    extends SettingsActivity
  {}
  
  public static class DateTimeSettingsActivity
    extends SettingsActivity
  {}
  
  public static class DevelopmentSettingsActivity
    extends SettingsActivity
  {}
  
  public static class DeviceAdminSettingsActivity
    extends SettingsActivity
  {}
  
  public static class DeviceInfoSettingsActivity
    extends SettingsActivity
  {}
  
  public static class DeviceSettings
    extends SettingsActivity
  {}
  
  public static class DisplaySettingsActivity
    extends RencentSettings
  {}
  
  public static class DisplaySizeAdaptionAppListActivity
    extends SettingsActivity
  {}
  
  public static class DomainsURLsAppListActivity
    extends SettingsActivity
  {}
  
  public static class DreamSettingsActivity
    extends RencentSettings
  {}
  
  public static class FingerprintEnrollSuggestionActivity
    extends FingerprintEnrollIntroduction
  {}
  
  public static class FingerprintSuggestionActivity
    extends FingerprintSettings
  {}
  
  public static class HighPowerApplicationsActivity
    extends SettingsActivity
  {}
  
  public static class HomeSettingsActivity
    extends SettingsActivity
  {}
  
  public static class IccLockSettingsActivity
    extends SettingsActivity
  {}
  
  public static class ImeiInformationActivity
    extends SettingsActivity
  {}
  
  public static class InputMethodAndLanguageSettingsActivity
    extends SettingsActivity
  {}
  
  public static class InputMethodAndSubtypeEnablerActivity
    extends SettingsActivity
  {}
  
  public static class KeyboardLayoutPickerActivity
    extends SettingsActivity
  {}
  
  public static class LocalePickerActivity
    extends SettingsActivity
  {}
  
  public static class LocationSettingsActivity
    extends RencentSettings
  {}
  
  public static class Lte4GEnableActivity
    extends RencentSettings
  {}
  
  public static class ManageAccountsSettingsActivity
    extends SettingsActivity
  {}
  
  public static class ManageApplicationsActivity
    extends RencentSettings
  {}
  
  public static class ManageAssistActivity
    extends SettingsActivity
  {}
  
  public static class ManagedProfileSettingsActivity
    extends SettingsActivity
  {}
  
  public static class MemorySettingsActivity
    extends RencentSettings
  {}
  
  public static class MobileNetworkMainActivity
    extends RencentSettings
  {}
  
  public static class NotificationAccessSettingsActivity
    extends SettingsActivity
  {}
  
  public static class NotificationAppListActivity
    extends SettingsActivity
  {}
  
  public static class NotificationStationActivity
    extends RencentSettings
  {}
  
  public static class OPAppLockerActivity
    extends SettingsActivity
  {}
  
  public static class OPApplicationsSettings
    extends SettingsActivity
  {}
  
  public static class OPButtonsSettingsActivity
    extends SettingsActivity
  {}
  
  public static class OPCloudServiceSettings
    extends SettingsActivity
  {}
  
  public static class OPDataSaverActivity
    extends SettingsActivity
  {}
  
  public static class OPDataUsageListSettings
    extends SettingsActivity
  {}
  
  public static class OPDataUsageMeteredSettings
    extends SettingsActivity
  {}
  
  public static class OPDataUsageSummaryActivity
    extends RencentSettings
  {}
  
  public static class OPDefaultHomeSettingsActivity
    extends SettingsActivity
  {}
  
  public static class OPFaceUnlockSettings
    extends SettingsActivity
  {}
  
  public static class OPFontStyleSettingsActivity
    extends SettingsActivity
  {}
  
  public static class OPGamingModeActivity
    extends SettingsActivity
  {}
  
  public static class OPGestureSettingsActivity
    extends SettingsActivity
  {}
  
  public static class OPNightModeActivity
    extends SettingsActivity
  {}
  
  public static class OPNotificationAndNotdisturbSettingsActivity
    extends SettingsActivity
  {}
  
  public static class OPOthersSettingsActivity
    extends SettingsActivity
  {}
  
  public static class OPPreInstalledAppListActivity
    extends SettingsActivity
  {}
  
  public static class OPProductInfoActivity
    extends SettingsActivity
  {}
  
  public static class OPQuickPaySettingsActivity
    extends SettingsActivity
  {}
  
  public static class OPReadingModeActivity
    extends SettingsActivity
  {}
  
  public static class OPRingModeActivity
    extends SettingsActivity
  {}
  
  public static class OPSilentModeActivity
    extends SettingsActivity
  {}
  
  public static class OPSimAndNetworkSettings
    extends SettingsActivity
  {}
  
  public static class OPStatusBarCustomizeSettingsActivity
    extends SettingsActivity
  {}
  
  public static class OPStorageSettingsSettings
    extends SettingsActivity
  {}
  
  public static class OneplusOTASettings
    extends SettingsActivity
  {}
  
  public static class OtherDeviceFunctionsSettingsActivity
    extends SettingsActivity
  {}
  
  public static class OtherSoundSettingsActivity
    extends SettingsActivity
  {}
  
  public static class OverlaySettingsActivity
    extends SettingsActivity
  {}
  
  public static class PaymentSettingsActivity
    extends SettingsActivity
  {}
  
  public static class PersonalSettings
    extends SettingsActivity
  {}
  
  public static class PhysicalKeyboardActivity
    extends SettingsActivity
  {}
  
  public static class PowerUsageSummaryActivity
    extends RencentSettings
  {}
  
  public static class PrintJobSettingsActivity
    extends SettingsActivity
  {}
  
  public static class PrintSettingsActivity
    extends SettingsActivity
  {}
  
  public static class PrivacySettingsActivity
    extends SettingsActivity
  {}
  
  public static class PrivateVolumeForgetActivity
    extends SettingsActivity
  {}
  
  public static class PrivateVolumeSettingsActivity
    extends SettingsActivity
  {}
  
  public static class ProfileMgrMainActivity
    extends RencentSettings
  {}
  
  public static class PublicVolumeSettingsActivity
    extends SettingsActivity
  {}
  
  public static class RoamingSettingsActivity
    extends RencentSettings
  {}
  
  public static class RunningServicesActivity
    extends SettingsActivity
  {}
  
  public static class SavedAccessPointsSettingsActivity
    extends SettingsActivity
  {}
  
  public static class ScreenLockSuggestionActivity
    extends ChooseLockGeneric
  {}
  
  public static class SecuritySettingsActivity
    extends SettingsActivity
  {}
  
  public static class SimSettingsActivity
    extends SettingsActivity
  {}
  
  public static class SimStatusActivity
    extends SettingsActivity
  {}
  
  public static class SoundSettingsActivity
    extends RencentSettings
  {}
  
  public static class SpellCheckersSettingsActivity
    extends SettingsActivity
  {}
  
  public static class StatusActivity
    extends SettingsActivity
  {}
  
  public static class StorageSettingsActivity
    extends SettingsActivity
  {}
  
  public static class StorageUseActivity
    extends SettingsActivity
  {}
  
  public static class SystemSettings
    extends SettingsActivity
  {}
  
  public static class SystemUpdateActivity
    extends SettingsActivity
  {}
  
  public static class TestingSettingsActivity
    extends SettingsActivity
  {}
  
  public static class TetherSettingsActivity
    extends RencentSettings
  {}
  
  public static class TextToSpeechSettingsActivity
    extends SettingsActivity
  {}
  
  public static class TimerSwitchSettingsActivity
    extends SettingsActivity
  {}
  
  public static class TopLevelSettings
    extends SettingsActivity
  {}
  
  public static class TrustedCredentialsSettingsActivity
    extends RencentSettings
  {}
  
  public static class UsageAccessSettingsActivity
    extends SettingsActivity
  {}
  
  public static class UsbSettingsActivity
    extends SettingsActivity
  {}
  
  public static class UserDictionarySettingsActivity
    extends SettingsActivity
  {}
  
  public static class UserSettingsActivity
    extends SettingsActivity
  {}
  
  public static class VpnSettingsActivity
    extends RencentSettings
  {}
  
  public static class VrListenersSettingsActivity
    extends SettingsActivity
  {}
  
  public static class WallpaperSettingsActivity
    extends SettingsActivity
  {}
  
  public static class WallpaperSuggestionActivity
    extends SettingsActivity
  {}
  
  public static class WifiAPITestActivity
    extends SettingsActivity
  {}
  
  public static class WifiCallingSettingsActivity
    extends SettingsActivity
  {}
  
  public static class WifiCallingSuggestionActivity
    extends SettingsActivity
  {}
  
  public static class WifiDisplaySettingsActivity
    extends SettingsActivity
  {}
  
  public static class WifiInfoActivity
    extends SettingsActivity
  {}
  
  public static class WifiP2pSettingsActivity
    extends SettingsActivity
  {}
  
  public static class WifiSettingsActivity
    extends RencentSettings
  {}
  
  public static class WirelessSettings
    extends SettingsActivity
  {}
  
  public static class WirelessSettingsActivity
    extends SettingsActivity
  {}
  
  public static class WriteSettingsActivity
    extends SettingsActivity
  {}
  
  public static class ZenAccessSettingsActivity
    extends SettingsActivity
  {}
  
  public static class ZenModeAutomationSettingsActivity
    extends RencentSettings
  {}
  
  public static class ZenModeAutomationSuggestionActivity
    extends SettingsActivity
  {}
  
  public static class ZenModeEventRuleSettingsActivity
    extends SettingsActivity
  {}
  
  public static class ZenModeExternalRuleSettingsActivity
    extends SettingsActivity
  {}
  
  public static class ZenModePrioritySettingsActivity
    extends RencentSettings
  {}
  
  public static class ZenModeScheduleRuleSettingsActivity
    extends SettingsActivity
  {}
  
  public static class ZenModeSettingsActivity
    extends RencentSettings
  {}
  
  public static class ZenModeVisualInterruptionSettingsActivity
    extends RencentSettings
  {}
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\Settings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */