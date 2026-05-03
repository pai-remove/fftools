// app/src/main/aidl/com/axeron/fftools/IFFToolsService.aidl
package com.axeron.fftools;

interface IFFToolsService {
    // ── DPI ──────────────────────────────────────────
    void setDpi(int dpi);
    int  getCurrentDpi();
    void resetDpi();

    // ── Refresh Rate ─────────────────────────────────
    void setRefreshRate(int rate);
    int  getMaxRefreshRate();

    // ── Background Apps ───────────────────────────────
    void killBackgroundApps();

    // ── Touch Sensitivity ─────────────────────────────
    void setTouchSensitivity(int level);  // 0=normal, 1=high, 2=extreme

    // ── Secure / System Settings ──────────────────────
    void writeSecureSetting(String key, String value);
    void writeSystemSetting(String key, String value);
    String readSecureSetting(String key);

    // ── Aspect Ratio ─────────────────────────────────
    void setDisplayDensity(int dpi);
    void resetDisplayDensity();

    // ── Utils ─────────────────────────────────────────
    int  getUid();
    boolean isAlive();
}
