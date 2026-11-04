package com.venus.zerocache.batching;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExactFitSelector implements BatchSelector {

 
    @Override
    public List<String> pickBatch(List<PendingRequest> pending, int budget) {
        Map<Integer, String> tokensSeen = new HashMap<>(); 

        for (PendingRequest request : pending) {
            int complement = budget - request.tokens();
            String other = tokensSeen.get(complement);
            if (other != null) {
                return List.of(other, request.id());   
            }
            
            tokensSeen.putIfAbsent(request.tokens(), request.id());
        }
        return List.of();
    }
    
}
