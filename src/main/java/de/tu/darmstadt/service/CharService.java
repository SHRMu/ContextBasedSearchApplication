package de.tu.darmstadt.service;

import de.tu.darmstadt.Utils.MapUtil;
import org.springframework.stereotype.Service;
import org.tensorflow.Tensor;

import java.util.*;

@Service
public class CharService {

    public String guessWord(ArrayList<Integer> chars_index, Integer inputChar){
        if (inputChar == 0)
            return "";
        ArrayList<Integer> indexList = new ArrayList<>();
        indexList.addAll(chars_index);
        indexList.add(inputChar);
        float[][] inputs = getInputChar(indexList);
        ArrayList<Integer> nextChar = getNextChar(inputs);
        while (nextChar.get(0)!= 0){
            indexList.add(nextChar.get(0));
            inputs = getInputChar(indexList);
            nextChar = getNextChar(inputs);
        }
        System.out.println(indexList);
        StringBuilder sb = new StringBuilder();
        for (Integer index:
                indexList) {
            if (index != 0)
                sb.append(ModelPredService.int2char.get(index));
        }
        System.out.println(sb);
        if (ModelPredService.word2int.get(sb.toString())!= null){
            return sb.toString();
        }else
            return "";
    }

    public float[][] getInputChar(ArrayList chars_index){
        int size = chars_index.size();
        while (size<5){
            chars_index.add(0,0);
            size = chars_index.size();
        }
        List<Integer> sub_index = chars_index.subList(size - 5, size);
        float[][] inputs = new float[1][5];
        for (int i = 0; i < 5; i++) {
            inputs[0][i] = sub_index.get(i);
        }
        return inputs;
    }

    public ArrayList<Integer> getNextChar(float[][] inputs){
        ArrayList<Integer> hits = new ArrayList<>();
        Tensor tensor = Tensor.create(inputs);
        Tensor output = ModelPredService.charSess.runner().feed("embedding_1_input_1", tensor).fetch("dense_1_1/Softmax").run().get(0);
        float [][] ans = new float[1][37];
        output.copyTo(ans);
        Map<Integer, Float> map = new HashMap<>();
        for (int i = 0; i < 37; i++) {
            map.put(i,ans[0][i]);
        }
        Map<Integer, Float> sortedMap = MapUtil.sortByValue(map);
        System.out.println(sortedMap);
        Set<Integer> keySet = sortedMap.keySet();
        Iterator<Integer> iterator = keySet.iterator();
        int index;
        while (iterator.hasNext() && (sortedMap.get(index = iterator.next()) > 0.1)){
            hits.add(index);
        }
        System.out.println(hits);
        return hits;
    }
}
