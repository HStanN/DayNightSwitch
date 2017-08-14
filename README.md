### DayNightSwitch
仿IOS版的[DayNightSwitch](https://github.com/finngaida/DayNightSwitch)

![Source](https://github.com/HStanN/DayNightSwitch/blob/master/image/switch.gif)

#### 使用:
```xml
        <com.hug.daynightswitch.DayNightSwitch
        android:id="@+id/day_night"
        android:layout_width="80dp"
        android:layout_height="40dp"
        app:status="day"
        />
```
![day](https://github.com/HStanN/DayNightSwitch/blob/master/image/day.png)

```xml
        <com.hug.daynightswitch.DayNightSwitch
        android:id="@+id/day_night"
        android:layout_width="80dp"
        android:layout_height="40dp"
        app:status="night"
        />
```
![night](https://github.com/HStanN/DayNightSwitch/blob/master/image/night.png)
#### 设置回调:
```java
     dayNightSwitch.setOnDayNightChangeListener(new DayNightSwitch.OnDayNightChangeListener() {
            @Override
            public void onCheck(int status, boolean checked) {
                //TODO
            }
        });
```
