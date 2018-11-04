package cn.acewill.mobile.pos.interfices;

/**
 * Created by DHH on 2016/8/23.
 * 检测有没有某权限
 */
public interface PermissionCallback<T>  {
    /**
     * 有权限
     */
    public void havePermission();
    /**
     * 没有权限
     */
    public void withOutPermission();
}
