//
//  ContentView.swift
//  one sentence
//
//  Created by Jackson Chen on 5/2/22.
//

import SwiftUI

struct ContentView: View {
    @EnvironmentObject var listViewModel : ListViewModel
    
    var body: some View {
        ZStack {
            if listViewModel.sentences.isEmpty {
                Text("There is no sentence📝")
                    .transition(AnyTransition.opacity.animation(.easeIn))
            } else {
                List() {
                    ForEach(listViewModel.sentences) {sentence in
                        ListRowView(sentence: sentence)
                    }
                    .onDelete(perform: listViewModel.deleteSent)
                    .onMove(perform: listViewModel.moveSent)
                    
                }
                .listStyle(PlainListStyle())
            }
        }
        .navigationTitle("One sentence")
        .navigationBarItems(leading: EditButton())
        .toolbar {
            ToolbarItemGroup (placement: .bottomBar) {
                NavigationLink(destination: GearView(), label: {
                    Image(systemName: "gearshape")
                })
                NavigationLink(destination: AddView(), label: {
                    Image(systemName: "plus")
                })
                
            }
            
        }
        
    }
}
struct GearView : View {
    var body : some View {
        VStack {
            List {
                NavigationLink(destination: AboutView(), label: {
                    Image(systemName: "questionmark.circle")
                    Text("About")
                })
            }
        }
        .navigationTitle("Setting")
    }
}


struct AboutView : View {
    var body : some View {
        VStack {
            Text("This is the One Sentence ios version.")
            Text("The origin Andriod version come from [@Kenmizz](https://github.com/KenMizz/Onesentence)")
        }
        .navigationTitle("About it")
        .offset(y:-30)
    }
}



struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        NavigationView{
            ContentView()
                .previewDevice("iPhone 13 Pro")
        }
        .environmentObject(ListViewModel())
    }
}
