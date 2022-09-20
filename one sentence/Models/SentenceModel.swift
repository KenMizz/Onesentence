//
//  Sentence.swift
//  one sentence
//
//  Created by Jackson Chen on 5/2/22.
//

import Foundation

struct SentenceModel : Identifiable, Codable{
    let id : String
    let sentence : String
    let createDate : String
    
    init (id: String = UUID().uuidString , sentence: String, createDate: String) {
        self.id = id
        self.sentence = sentence
        self.createDate = createDate
    }
    func updateSentence () -> SentenceModel {
        return SentenceModel(id: id, sentence: sentence, createDate: createDate)
    }
}
