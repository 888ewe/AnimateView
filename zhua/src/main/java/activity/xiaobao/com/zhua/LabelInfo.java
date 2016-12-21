package activity.xiaobao.com.zhua;

/**
 * Created by gzjck on 2015/10/21.
 */
public class LabelInfo {
    public static int EDIT_ACTION_MODIFY = 0;
    public static int EDIT_ACTION_ADD = 1;
    public static int EDIT_ACTION_DEL = 2;

    public int editAction;

    public String title2Text;
    public String title1Text;
    public String title3Text;

    public String input1Text;
    public String input2Text;
    public String input3Text;

    /**
     * label在pointAt的x坐标(父容器中的像素值)
     */
    public float pxX = -1;
    /**
     * label在pointAt的y坐标(父容器中的像素值)
     */
    public float pxY = -1;
    /**
     * label在pointAt的x坐标(父容器中的百分比)
     */
    public float pcX = -1;
    /**
     * label在pointAt的y坐标(父容器中的百分比)
     */
    public float pcY = -1;

}
