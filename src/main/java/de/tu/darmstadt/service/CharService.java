package de.tu.darmstadt.service;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CharService {

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

}
