package zk;

/**
 * Created by gongp on 2017/8/9.
 */
public class One {
    public int a(){
        return 1;
    }
}

class Two extends One{
    public int a(){
        return 3;
    }

    public void a(int a){
        return ;
    }
}