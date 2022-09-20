//
//  AddView.swift
//  one sentence
//
//  Created by Jackson Chen on 5/2/22.
//

import SwiftUI

struct AddView : View {
    @Environment(\.presentationMode) var presentationMode
    @EnvironmentObject  var listViewModel : ListViewModel
    @State private var tempStr = ""
    @State private var tempDate = Date.now
    @State var alertTitle : String = ""
    @State var showAlert : Bool = false
    
    var body : some View {
        VStack{
            Form {
                Section(header: Text("add sentence")) {
                    TextField("Sentence", text: $tempStr)
                        .padding()
                    DatePicker("Date", selection: $tempDate)
                }
                
                Button (action: saveButtonPress, label: {
                    Text("Add")
                        .font(.headline)
                    
                })
                
            }
            
        }
        .navigationTitle("Adding")
        .alert(isPresented: $showAlert, content: getAlert)
    }
    func saveButtonPress() {
        if textIsLongEnough() {
            listViewModel.addSent(sent: tempStr)
            //tell the presentationMode go back one in the view hierarchy
            presentationMode.wrappedValue.dismiss()
        }
    }
    func textIsLongEnough() -> Bool {
        if tempStr.count < 2 {
            alertTitle = "Your sentence must at least 2 characters!!!😀"
            showAlert.toggle()
            return false
        }
        return true
    }
    func getAlert() -> Alert {
        return Alert(title: Text(alertTitle))
    }
}

struct AddView_Previews: PreviewProvider {
    static var previews: some View {
        NavigationView{
        AddView()
        }.environmentObject(ListViewModel())
    }
}
