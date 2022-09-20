//
//  one_sentenceApp.swift
//  one sentence
//
//  Created by Jackson Chen on 5/2/22.
//

import SwiftUI

@main
struct one_sentenceApp: App {
    @StateObject var listViewModel : ListViewModel = ListViewModel()
    var body: some Scene {
        WindowGroup {
            NavigationView{
            ContentView()
            }
            .environmentObject(listViewModel)
        }
    }
}
