import java.util.Random;

/**
 * Created by wenjusun on 8/28/2017.
 */
public class RandomTest {

    public static void main(String args[]){

        Random random = new Random();
        for (int i=0;i<5;i++){
            System.out.println(random.nextInt(10));

        }

        for (int i=0;i<5;i++){
            System.out.println(Math.random());
        }

    }

}
