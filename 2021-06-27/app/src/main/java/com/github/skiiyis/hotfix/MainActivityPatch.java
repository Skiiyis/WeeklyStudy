package com.github.skiiyis.hotfix;

public class MainActivityPatch implements HotFixProxy {

    @Override
    public Object fix(Object origin, String methodDesc) {
        if (methodDesc.equals("problemMethod()Ljava.lang.String;")){
            return ((MainActivity) origin).noProblemMethod();
        }
        return null;
    }

    @Override
    public boolean checkFix(Object origin, String methodDesc) {
        return methodDesc.equals("problemMethod()Ljava.lang.String;");
    }
}
