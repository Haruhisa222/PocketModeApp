#  PocketModeApp for Android

> A lightweight Android service that temporarily blocks screen interaction from the **notification bar** — perfect for keeping your phone safe in your pocket while playing videos or music.  

通知バーから起動し、画面操作を一時的にブロックできるAndroidアプリです。  
YouTubeなどを再生したままポケットに入れても誤タッチしません。

---

##  Features / 機能

-  **Toggle Lock Mode** directly from the notification bar  
  通知バーからロック・解除をワンタップで切り替え
-  **Overlay-based lock screen** using Android's system overlay  
  オーバーレイを利用して画面操作をブロック
-  **Runs as a Foreground Service** for stability and persistence  
  フォアグラウンドサービスで安定稼働
-  Supports Android 13+ notification permission  
  Android 13以降の通知権限にも対応

---

##  How It Works / 動作概要

1. アプリを起動すると、`PocketService` がフォアグラウンドサービスとして起動します。  
2. 通知バーの「ロック」ボタンを押すと、黒いオーバーレイが表示され画面操作が無効化されます。  
3. 「解除」ボタンで再び操作可能になります。  
4. オーバーレイ権限が必要な場合は、自動的に設定画面を開きます。

---

##  Tech Overview / 技術概要

| Component | Description |
|------------|-------------|
| `MainActivity` | 起動時に通知権限をチェックし、`PocketService` を開始 |
| `PocketService` | Foreground Service。通知・ロック切り替え・オーバーレイ表示を担当 |
| `ToggleReceiver` | 通知ボタンからのBroadcastを受信し、サービスにトグル命令を送信 |

---

##  Permissions / 権限

```xml
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
