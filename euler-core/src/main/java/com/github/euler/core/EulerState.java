package com.github.euler.core;

public class EulerState {

    private boolean processing = false;

    public boolean isProcessing() {
        return processing;
    }

//    public void onMessage(EvidenceToProcess etp) throws ProcessingAlreadyStarted {
//        if (processing) {
//            throw new ProcessingAlreadyStarted();
//        }
//        this.processing = true;
//    }
//
//    public void onMessage(EvidenceItemFound msg) {
//        // TODO Auto-generated method stub
//        
//    }

}
