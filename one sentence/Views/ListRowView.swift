//
//  ListRowView.swift
//  one sentence
//
//  Created by Jackson Chen on 5/3/22.
//

import SwiftUI

struct ListRowView: View {
    let sentence : SentenceModel
    var body: some View {
            VStack (alignment: .leading, spacing: 5) {
                Text(sentence.sentence)
                    .fontWeight(.semibold)
                Text(sentence.createDate)
                    .font(.subheadline)
                    .foregroundColor(.secondary)
            }.padding(.vertical, 8)
    
}
}
