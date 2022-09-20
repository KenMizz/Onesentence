//
//  SentenceDetailView.swift
//  one sentence
//
//  Created by Jackson Chen on 5/2/22.
//

import SwiftUI

struct SentenceDetailView: View {
    var sentence : SentenceModel
    @State private var localSent : String = ""
    var body: some View {
        VStack {
            
            Form {
//               TextField(localSent, $localSent)
//                Text(sentence.sentence)
//                TextField(sentence.sentence, text:$localSent)
                TextEditor(text: $localSent)
            }
        }
    }
}

//struct SentenceDetailView_Previews: PreviewProvider {
//    static var previews: some View {
//        SentenceDetailView(sentence: Sentence)
//    }
//}
