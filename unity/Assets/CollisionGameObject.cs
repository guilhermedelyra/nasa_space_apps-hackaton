using UnityEngine;
using System;
using UnityEngine.UI;
using TMPro;

public class CollisionGameObject : MonoBehaviour
{
    public GameObject canvas;
    public TextMeshProUGUI radiation, sound, tempo, member_title;
    public Image bar_radiation, bar_sound, bar_tempo;

    void OnTriggerEnter(Collider collision)
    {
        Debug.Log("entrou " + collision.gameObject.tag);
        if (collision.gameObject.tag == "maodedo")
        {
            canvas.SetActive(!canvas.activeSelf);
            if (canvas.activeSelf) {
                System.Random rnd = new System.Random();
                int random = rnd.Next(1000, 7000);
                int randomT = rnd.Next(40, 60);
                int randomS = rnd.Next(30, 140);
                radiation.text = random.ToString() + "sV";
                sound.text = randomS.ToString() + "db";
                tempo.text = randomT.ToString() + "s";

                float hue = (255*random)/7000;
                float hue1 = (255*randomS)/140;
                float hu2 = (255*randomT)/60;
                bar_sound.fillAmount = hue1/255;
                bar_tempo.fillAmount = hu2/255;
                bar_radiation.fillAmount = hue/255;
                
                radiation.color = new Color32((byte)(hue), (byte)(255 - hue), 0, 255);
                sound.color = new Color32((byte)(hue), (byte)(255 - hue1), 0, 255);
                member_title.text = char.ToUpper(gameObject.name.Split('_')[1][0])+gameObject.name.Split('_')[1].Substring(1);
            }
        }
    }
}