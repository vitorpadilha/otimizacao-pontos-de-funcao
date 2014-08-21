package br.uniriotec.vitor.padilha.dissertacao;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import br.uniriotec.vitor.padilha.dissertacao.exception.CloneException;
import br.uniriotec.vitor.padilha.dissertacao.exception.ElementException;
import br.uniriotec.vitor.padilha.dissertacao.jaxb.MyValidationEventHandler;
import br.uniriotec.vitor.padilha.dissertacao.model.FunctionPointSystem;
import br.uniriotec.vitor.padilha.dissertacao.model.dataModel.DET;
import br.uniriotec.vitor.padilha.dissertacao.model.dataModel.DataModelElement;
import br.uniriotec.vitor.padilha.dissertacao.model.dataModel.RET;
import br.uniriotec.vitor.padilha.dissertacao.model.stakeholdersInterests.StakeholderInterests;
import br.uniriotec.vitor.padilha.dissertacao.utils.FunctionsPointReader;

public class CriaFuncoesDeDadosNaIntancia {
	public static final String INSTANCE_PATH = "ACAD";
	public static final String HTML_RESULT_PATH = (new File("")).getAbsolutePath()+"\\instancias\\";
	
	public static void main(String[] args) throws JAXBException, IOException, ElementException, CloneException {
		JAXBContext context = JAXBContext.newInstance(FunctionPointSystem.class);
		JAXBContext contextInterests = JAXBContext.newInstance(StakeholderInterests.class);
		

		Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		Unmarshaller um = context.createUnmarshaller();
		um.setEventHandler(new MyValidationEventHandler());
		Unmarshaller um2 = contextInterests.createUnmarshaller();
		um2.setEventHandler(new MyValidationEventHandler());
		File file = new File("");
		FunctionsPointReader reader = new FunctionsPointReader(file.getAbsolutePath()+"\\instancias\\"+INSTANCE_PATH+"\\functions-point.xml",file.getAbsolutePath()+"\\instancias\\"+INSTANCE_PATH+"\\stakeholders-interest.xml");
		FunctionPointSystem functionPointSystem = reader.read();
		functionPointSystem.charge();
		if(functionPointSystem.validate()) {
			FunctionPointSystem clone1 = (FunctionPointSystem) functionPointSystem.cloneElement();
			int maiorNumeroRETS = 0;
			int menorNumeroRETS = 999999;
			int maiorNumeroDETS = 0;
			int menorNumeroDETS = 999999;
			for(DataModelElement dataModelElement:clone1.getDataModel().getDataModelElements()){
				int rets = (int) (dataModelElement.getRets().size() * ((Math.random() * 1)));
				
				if(dataModelElement.getRets().size()>maiorNumeroRETS)
					maiorNumeroRETS = dataModelElement.getRets().size();
				if(dataModelElement.getRets().size()<menorNumeroRETS)
					menorNumeroRETS = dataModelElement.getRets().size();
				for (RET ret : dataModelElement.getRets()) {
					int dets = (int) (ret.getDets().size() * ((Math.random() * 1)));
					if(ret.getDets().size()>maiorNumeroDETS)
						maiorNumeroDETS = ret.getDets().size();
					if(ret.getDets().size()<menorNumeroDETS)
						menorNumeroDETS = ret.getDets().size();
					criaDETs(dataModelElement, ret, dets);
				}
				for (int i = 0; i < rets; i++) {
					RET ret = new RET();
					ret.setParent(dataModelElement);
					ret.setName("RET"+i);
					int dets = (int) (menorNumeroDETS+ (Math.random() * (maiorNumeroDETS - menorNumeroDETS)));
					criaDETs(dataModelElement, ret, dets);
					dataModelElement.getRets().add(ret);
				}				
			}
			
			
			
//			
//			FunctionPointSystem clone2 = (FunctionPointSystem) functionPointSystem.cloneElement();
//			int fds = (int) (Math.random() * clone2.getDataModel().getDataModelElements().size());
//			for (int i = 0; i < fds; i++) {
//				DataModelElement dataModelElement;
//				if(Math.random()*2 <=1.35) {
//					dataModelElement = new ILF();
//					dataModelElement.setType(DataModelElementType.ILF);
//					dataModelElement.setName("ILF"+i);
//				}
//				else {
//					dataModelElement = new EIF();
//					dataModelElement.setType(DataModelElementType.EIF);
//					dataModelElement.setName("EIF"+i);
//				}
//				dataModelElement.setParent(clone2.getDataModel());
//				dataModelElement.setRets(new ArrayList<RET>());
//				int rets = (int) (menorNumeroRETS+ (Math.random() * (maiorNumeroRETS - menorNumeroRETS)));
//				for (int j = 0; j < rets; j++) {
//					RET ret = new RET();
//					ret.setParent(dataModelElement);
//					ret.setName("RET"+i);
//					int dets = (int) (menorNumeroDETS+ (Math.random() * (maiorNumeroDETS - menorNumeroDETS)));
//					criaDETs(dataModelElement, ret, dets);
//					dataModelElement.getRets().add(ret);
//				}
//				clone2.getDataModel().getDataModelElements().add(dataModelElement);
//				for (Transaction transaction : clone2.getTransactionModel().getTransactions()) {
//					
//				}
//			}
		}
	
	}

	private static void criaDETs(DataModelElement dataModelElement, RET ret,
			int dets) {
		for (int i = 0; i <dets; i++) {
			DET det = new DET();
			det.setDataModelElement(dataModelElement.getName());
			det.setName(ret.getName()+"RET"+i);
			det.setParent(ret);
			if(ret.getDets()==null)
				ret.setDets(new ArrayList<DET>());
			ret.getDets().add(det);
		}
	}
}
