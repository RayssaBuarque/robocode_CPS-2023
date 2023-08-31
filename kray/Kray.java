/*
EQUIPE KRAY - Etec Abdias do Nascimento
	Evelyn Alves Oliveira
 	Klayvem Guimarães Leal da Silva
  	Rayssa Buarque Malheiros
*/

package kray;

import robocode.HitRobotEvent;
import robocode.Robot;
import robocode.*;

import java.awt.*;

public class Kray extends Robot 
{
	boolean limite; // Não vire se não houver um robo ali
	double qtdMovimento; // Quanto se mover
	double passo = 0; //Registro de quantos passos até o limite ele já andou
	String ultimoInimigo = ""; //armazena o nome do último inimigo a nos balear

	//main loop
	public void run() {

		// Inicialize o robo
		inicializeRobo();
		
		//ande o quanto for necessario pra ficar rente a parede
		ahead(qtdMovimento);
		
		// vire à direita em 90 graus.
		limite = true;
		turnRight(90);

		// movimente o robo sem parar
		while (true) {
			passo = 0;					
			movimentos();	

		}
	}

	//normalizar angulo da arma	
	public double normalizeBearing(double angulo) {
		while (angulo >  180) {
			angulo -= 360;
		}
		while (angulo < -180) {
			angulo += 360;
		}
		return angulo;
	}

	//Caso o bot for atingido por uma bala
	@Override
	public void onHitByBullet(HitByBulletEvent e){
		stop();
		
		//se quem atirou foi o mesmo do último tiro, é provável que ele não saiu do lugar,
		//nisso, o bot vira a arma e manda uma bala potente
		if(ultimoInimigo == e.getName()){
			double roda = getHeading() - getGunHeading() + e.getBearing();
			turnGunRight(normalizeBearing(roda));
			fire(1.5);
			fire(1.5);
			ultimoInimigo = "";
		}
		//o robô vira pra direita e corre
		turnRight(90);
		ahead(200);
		passo += 200;
		ultimoInimigo = e.getName(); //armazene o nome do meliante
	} 

	//onHitRobot:  mova-se se tu bater em alguém
	@Override
	public void onHitRobot(HitRobotEvent cicero) {	
		stop();	
		
		if(getGunHeading() != cicero.getBearing()){			
			double turn = getHeading() - getGunHeading() + cicero.getBearing();
			// normalize the turn to take the shortest path there
			turnGunRight(normalizeBearing(turn));
		}
		
		fire(3);
	}

	//Subrotina Caso um Robô inimigo for Scaneado
	@Override
    public void onScannedRobot(ScannedRobotEvent inimigo) {
        stop();
		//Pegar a distancia do inimigo pro nosso bot
		double distancia = inimigo.getDistance();
		
		//Pegando as energias dos bots pra comparar elas depois
		double nossaEnergia = getEnergy();
		double energiaInimigo = inimigo.getEnergy();

        double anguloScanner = inimigo.getBearing();
        double anguloArma = getGunHeading() - getHeading();
		double qtdGiroCertaArma = anguloScanner - anguloArma;

		//Esse trecho de loops garante que a arma vai mirar o alvo do jeito mais rápido possível. 
        while(qtdGiroCertaArma > 180) {
            qtdGiroCertaArma = qtdGiroCertaArma - 360;
        }
        while(qtdGiroCertaArma < -180) {
            qtdGiroCertaArma = qtdGiroCertaArma + 360;
		}
		
		//girando a arma e atirando se o bot estiver perto o suficiente
		if ( distancia < 150 ){ 
        	turnGunRight(qtdGiroCertaArma);
			fire(2);
			fire(1.5);
			
			//ao atirar, ele dá uns passos pra trás só pra garantir que não vai tomar um tiro de volta		
			turnRight(90);
			ahead(-100);
			passo -= 100; 		
		}else { 		
			// Se nossa energia for maior, atire
			if(nossaEnergia > energiaInimigo){
				turnGunRight(qtdGiroCertaArma);
				fire(2);
				fire(0.5);
			
				//ao atirar, ele dá uns passos pra trás só pra garantir que não vai tomar um tiro de volta
				ahead(-50);
				passo -= 50;
			}
			//Se não, fuja
			else{
           	 	// Ângulo para fugir perpendicularmente
            	turnGunRight(qtdGiroCertaArma);
				fire(1);
			
				//ao atirar, ele dá uns passos pra trás só pra garantir que não vai tomar um tiro de volta
				ahead(-100);
				passo -= 100;
			}
        
		}
        return;
    }	

	//MÉTODO QUE SETA AS CONFIGURAÇÕES INICIAIS BÁSICAS DO ROBÔ
	private void inicializeRobo() {
		
		// Setar cores robo
		setBodyColor(Color.black);
		setGunColor(Color.black);
		setRadarColor(Color.red);
		setBulletColor(Color.red);
		setScanColor(Color.red);
		
		// Inicialize qtdMovimento para as dimensões máximas da arena.
		qtdMovimento = Math.max(getBattleFieldWidth(), getBattleFieldHeight());

		limite = false;
		// vire para encarar a parede mais próxima.
		turnLeft(getHeading() % 90);
		turnGunRight(90);
		
		// setando a arma do robo pra girar de forma independente do corpo
		setAdjustRadarForGunTurn(true);
		setAdjustGunForRobotTurn(true);
	}
	
	//subrotina de MOVIMENTAÇÃO do bot
	private void movimentos(){
		
		while(passo <qtdMovimento){
			// ande reto parede
			ahead(200);
			passo += 200;
			
			// scaneando a situação 
			scan();
		}
			
		// vire para a próxima parede (direita)
		turnRight(90);
	}
	
}
