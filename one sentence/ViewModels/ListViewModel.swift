//
//  ListViewModel.swift
//  one sentence
//
//  Created by Jackson Chen on 5/3/22.
//

import Foundation
/*
 CRUD FUNCTION
 
 Create
 Read
 Delete
 Update
 */
class ListViewModel : ObservableObject {
    @Published var sentences : [SentenceModel] = [] {
        //everytime we did change to the array, this didSet get call
        didSet {
            saveSent()
        }
    }
    let SentencesKey : String = "sentences_list"
    
    init() {
        getSentences()
    }
    
    func getSentences() {
        guard
            let data = UserDefaults.standard.data(forKey: SentencesKey),
            let saveSents = try? JSONDecoder().decode([SentenceModel].self,from : data)
        else {return}
        self.sentences = saveSents
    }
    func deleteSent(indexSet: IndexSet) {
        sentences.remove(atOffsets: indexSet)
    }
    func moveSent(from: IndexSet, to: Int) {
        sentences.move(fromOffsets: from, toOffset: to)
    }
    func addSent(sent: String) {
        //TODO test here
        let newSentence = SentenceModel(sentence: sent, createDate: "Jan 14")
        sentences.append(newSentence)
    }
    func updateSent(sentence : SentenceModel) {
        //TODO fix it
        //        let index = sentence.sentence.firstIndex(where: {sentence.id == sentence.id})
        //        sentences[index] = sentence.updateSentence()
        
    }
    func saveSent() {
        if let encodedData = try? JSONEncoder().encode(sentences) {
            UserDefaults.standard.set(encodedData, forKey: SentencesKey)
        }
    }
}
