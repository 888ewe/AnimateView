package activity.xiaobao.com.zhua;

import android.app.Activity;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends Activity {

    private RelativeLayout viewMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewMain = (RelativeLayout) findViewById(R.id.viewMain);
        test();
    }

    private void test() {
        // label1
        LabelInfo info = new LabelInfo();
        info.title1Text = "t111";
        info.title2Text = "t22222";
        info.title3Text = "3333";
        info.pcX = 0.1f;
        info.pcY = 0.1f;
        ZhuaView label = new ZhuaView(this);
        label.setLabelInfo(info);
        label.currStyle=23;
        label.setOnTextClickListener(new ZhuaView.OnTextClickListener() {
            @Override
            public void onTextClick(int position) {
                Toast.makeText(MainActivity.this, "po" + position, Toast.LENGTH_SHORT).show();
            }
        });
        label.alwaysWave();

        viewMain.addView(label);
        // label1
        LabelInfo info2 = new LabelInfo();
        info2.title1Text = "t111";
        info2.title2Text = "t22222";
        info2.title3Text = "3333";
        info2.pcX = 0.9f;
        info2.pcY = 0.9f;
        ZhuaView label2 = new ZhuaView(this);
        label2.setLabelInfo(info);
        label2.currStyle=22;
        label2.setOnTextClickListener(new ZhuaView.OnTextClickListener() {
            @Override
            public void onTextClick(int position) {
                Toast.makeText(MainActivity.this, "po" + position, Toast.LENGTH_SHORT).show();
            }
        });
        label2.alwaysWave();

        viewMain.addView(label2);

    }
}
